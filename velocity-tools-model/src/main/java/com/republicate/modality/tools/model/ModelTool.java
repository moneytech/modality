package com.republicate.modality.tools.model;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.republicate.modality.Attribute;
import com.republicate.modality.Entity;
import com.republicate.modality.Instance;
import com.republicate.modality.Model;
import com.republicate.modality.ModelRepository;
import com.republicate.modality.RowAttribute;
import com.republicate.modality.RowsetAttribute;
import com.republicate.modality.ScalarAttribute;
import com.republicate.modality.config.Constants;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>ModelTool</p>
 *
 * @author Claude Brisson
 * @version $Revision: $
 * @since VelocityTools 3.1
 */

@ValidScope(Scope.APPLICATION)
@DefaultKey("model")
public class ModelTool extends SafeConfig implements Constants, Serializable
{
    public static final String MODEL_TOOLS_DEFAULTS_PATH = "com/republicate/modality/tools/model/tools.xml";

    /* TODO *
     * Configuration entry point. Can only be called once.
     * @param params configuration values map
     * /
    public void configure(Map<String, Object> params)
    {
        String loggerName = new ConfigHelper(params).getString(MODEL_LOGGER_NAME);
        if (loggerName != null)
        {
            params.put(SafeConfig.LOGGER_NAME_KEY, loggerName);
        }
        super.configure(params);
    }
    */

    @Override
    protected void configure(ValueParser params)
    {
        model = createModel();

        // handles default and global configuration ;
        // all those values can be changed during initialization
        // when model path is read
        model.configure(params);

        String key = params.getString("key");
        if (key == null || key.length() == 0)
        {
            key = "default";
        }
        model.initialize(key, model.getDefinition());
        canWrite = model.getWriteAccess() == Model.WriteAccess.VTL;
    }

    protected Model createModel()
    {
        return new Model();
    }

    protected Model getModel()
    {
        return model;
    }

    public Serializable evaluate(String name, Serializable... params)
    {
        try
        {
            return getModel().evaluate(name, params);
        }
        catch (SQLException sqle)
        {
            error("could not evaluate property {}", name, sqle);
            return null;
        }
    }

    public Serializable evaluate(String name, Map params)
    {
        try
        {
            return getModel().evaluate(name, params);
        }
        catch (SQLException sqle)
        {
            error("could not evaluate property {}", name, sqle);
            return null;
        }
    }

    public InstanceReference retrieve(String name, Serializable... params)
    {
        try
        {
            Instance instance = getModel().retrieve(name, params);
            return instance == null ? null : createInstanceReference(instance);
        }
        catch (SQLException sqle)
        {
            error("could not retrieve property {}", name, sqle);
            return null;
        }
    }

    public InstanceReference retrieve(String name, Map params)
    {
        try
        {
            Instance instance = getModel().retrieve(name, params);
            return instance == null ? null : createInstanceReference(instance);
        }
        catch (SQLException sqle)
        {
            error("could not retrieve property {}", name, sqle);
            return null;
        }
    }

    public Iterator<InstanceReference> query(String name, Serializable... params)
    {
        try
        {
            return createInstanceReferenceIterator(getModel().query(name, params));
        }
        catch (SQLException sqle)
        {
            error("could not iterate property {}", name, sqle);
            return null;
        }
    }

    public Iterator<InstanceReference> query(String name, Map params)
    {
        try
        {
            return createInstanceReferenceIterator(getModel().query(name, params));
        }
        catch (SQLException sqle)
        {
            error("could not iterate property {}", name, sqle);
            return null;
        }
    }

    public int perform(String name, Serializable... params)
    {
        try
        {
            if (!canWrite)
            {
                throw new SQLException("instance is read-only");
            }
            return getModel().perform(name, params);

        }
        catch (SQLException sqle)
        {
            error("could not perform action {}", name, sqle);
            return 0;
        }
    }

    public int perform(String name, Map params)
    {
        try
        {
            if (!canWrite)
            {
                throw new SQLException("instance is read-only");
            }
            return getModel().perform(name, params);

        }
        catch (SQLException sqle)
        {
            error("could not perform action {}", name, sqle);
            return 0;
        }
    }

    public Object get(String key)
    {
        try
        {
            Attribute attribute = model.getAttribute(key);
            if (attribute != null)
            {
                if (attribute instanceof ScalarAttribute)
                {
                    return ((ScalarAttribute)attribute).evaluate();
                }
                else if (attribute instanceof RowAttribute)
                {
                    Instance instance = ((RowAttribute)attribute).retrieve();
                    return instance == null ? null : createInstanceReference(instance);
                }
                else if (attribute instanceof RowsetAttribute)
                {
                    return createInstanceReferenceIterator(((RowsetAttribute)attribute).query());
                }
            }
            Entity entity = model.getEntity(key);
            if (entity != null)
            {
                return createEntityReference(entity);
            }
            return null;
        }
        catch (SQLException sqle)
        {
            error("could not get property {}", key, sqle);
            return null;
        }
    }

    protected EntityReference createEntityReference(Entity entity)
    {
        return new EntityReference(entity, this);
    }

    protected InstanceReference createInstanceReference(Instance instance)
    {
        return new InstanceReference(instance, this);
    }

    public Iterator<InstanceReference> createInstanceReferenceIterator(Iterator<Instance> query)
    {
        return new InstanceReferenceIterator(query);
    }

    protected void error(String message, Object... arguments)
    {
        // default implementation only logs
        getLogger().error(message, arguments);
    }

    protected Logger getLogger() // give package access to logger
    {
        return getLog();
    }

    public class InstanceReferenceIterator implements Iterator<InstanceReference>
    {
        public InstanceReferenceIterator(Iterator<Instance> iterator)
        {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public InstanceReference next()
        {
            return createInstanceReference(iterator.next());
        }

        protected Iterator<Instance> getInnerIterator()
        {
            return iterator;
        }

        private Iterator<Instance> iterator;
    }

    // serialization

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
        out.writeObject(model.getModelId());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        String modelId = (String)in.readObject();
        ModelRepository.getModel(modelId);
        if (model == null)
        {
            // lazy initialization
            ModelRepository.addModelListener(modelId, new LazyModelSetter());
            return;

        }
    }

    // last error handling

    protected static void setLastError(String message)
    {
        lastError.set(message);
    }

    public static String getLastError()
    {
        return lastError.get();
    }

    public static void clearLastError()
    {
        lastError.remove();
    }

    protected void setModel(Model model)
    {
        this.model = model;
    }

    private class LazyModelSetter implements Consumer<Model>
    {
        public LazyModelSetter()
        {
        }

        @Override
        public void accept(Model model)
        {
            setModel(model);
        }
    }

    private static ThreadLocal<String> lastError = new ThreadLocal<>();

    // private fields

    private transient Model model = null;

    private boolean canWrite = false;

}
