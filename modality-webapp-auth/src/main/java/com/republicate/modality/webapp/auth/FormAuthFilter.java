package com.republicate.modality.webapp.auth;

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

import com.republicate.modality.Instance;
import com.republicate.modality.Model;
import com.republicate.modality.webapp.WebappModelAccessor;
import com.republicate.modality.webapp.auth.helpers.CredentialsChecker;
import com.republicate.modality.webapp.auth.helpers.CredentialsCheckerImpl;

import java.util.Optional;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * <p>Authentication filter relying on a query returning a user instance whenever his/her credentials
 * are correct, using the <code>&lt;login/&gt;</code> and <code>&lt;password/&gt;</code> placeholder parameters.
 * A typical query would be:</p>
 * <pre><code>&lt;row result="user"&gt;
 *   select * from user where login = &lt;login/&gt; and password = sha1(&lt;password/&gt;)
 * &lt;/row&gt;</code></pre>
 * <p>Configuration parameters:</p>
 * <ul>
 *     <li>auth.model.<b>user_by_credentials</b>&nbsp;row attribute name to use ;
 *     defaults to <code>user_by_credentials</code>.</li>
 *     <li>auth.model.<b>refresh_rate</b>&nbsp;user instance refresh rate in seconds;
 *     defaults to 0 (aka never)</li>
 * </ul>
 * <p>As usual, configuration parameters can be filter's configure-params or global context-params, or inside <code>modality.properties</code>.</p>
 */

public class FormAuthFilter extends BaseFormAuthFilter<Instance> implements WebappModelAccessor
{
    public static final String USER_BY_CRED_ATTRIBUTE = "auth.model.user_by_credentials";
    public static final String USER_REFRESH_RATE =      "auth.model.refresh_rate";

    private static final String DEFAULT_MODEL_AUTH_USER_BY_CREDENTIALS = "user_by_credentials";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        super.init(filterConfig);
        String userByCredentialsAttribute = Optional.ofNullable(findConfigParameter(USER_BY_CRED_ATTRIBUTE)).orElse(DEFAULT_MODEL_AUTH_USER_BY_CREDENTIALS);
        credentialsChecker = new CredentialsCheckerImpl(userByCredentialsAttribute);
    }

    @Override
    public void modelInitialized(Model model) throws ServletException
    {
        super.modelInitialized(model);
        credentialsChecker.setModel(model);
    }

    @Override
    protected Instance checkCredentials(String login, String password) throws ServletException
    {
        getModel(); // force model initialization
        return credentialsChecker.checkCredentials(getRealm(), login, password);
    }

    protected String displayUser(Instance user)
    {
        String login = user.getString(getLoginField());
        if (login == null && !"login".equals(getLoginField()))
        {
            login = user.getString("login");
        }
        return login != null ? login : String.valueOf(user);
    }

    private CredentialsChecker<Instance> credentialsChecker = null;
}
