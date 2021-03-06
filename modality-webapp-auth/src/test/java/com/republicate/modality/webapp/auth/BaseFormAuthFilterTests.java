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
import com.republicate.modality.webapp.WebappModelProvider;
import com.republicate.modality.webapp.auth.helpers.SavedRequest;
import org.apache.velocity.tools.view.ServletUtils;
import org.apache.velocity.tools.view.VelocityView;
import org.easymock.Capture;

import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;

import static org.easymock.EasyMock.*;

public class BaseFormAuthFilterTests extends BaseWebBookshelfTests
{
    protected VelocityView velocityView = null;

    protected void recordVelocityConfig(boolean loadDefaults, String toolbox) throws Exception
    {
        expect(filterConfig.getInitParameter(ServletUtils.SHARED_CONFIG_PARAM)).andAnswer(eval("true"));
        expect(servletContext.getAttribute(ServletUtils.VELOCITY_VIEW_KEY)).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(ServletUtils.ALT_VELOCITY_VIEW_KEY)).andAnswer(eval("com.republicate.modality.webapp.ModalityView"));
        expect(filterConfig.getInitParameter(VelocityView.USER_OVERWRITE_KEY)).andAnswer(eval("true"));
        expect(servletContext.getInitParameter(VelocityView.PROPERTIES_KEY)).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(VelocityView.PROPERTIES_KEY)).andAnswer(eval(null));
        expect(servletContext.getResourceAsStream("/WEB-INF/velocity.properties")).andAnswer(eval(null));
        expect(servletContext.getResourceAsStream("/velocimacros.vtl")).andAnswer(eval(null));
        expect(servletContext.getResourceAsStream("/VM_global_library.vm")).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(VelocityView.LOAD_DEFAULTS_KEY)).andAnswer(eval(String.valueOf(loadDefaults)));
        expect(servletContext.getInitParameter(ServletUtils.CONFIGURATION_KEY)).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(ServletUtils.CONFIGURATION_KEY)).andAnswer(eval(null));
        if (toolbox == null)
        {
            expect(servletContext.getResource("/WEB-INF/tools.xml")).andAnswer(eval(null));
        }
        else
        {
            URL tools = getResource(toolbox);
            expect(servletContext.getResource("/WEB-INF/tools.xml")).andAnswer(eval(tools));
        }
        expect(servletContext.getAttribute(ServletUtils.CONFIGURATION_KEY)).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(VelocityView.CLEAN_CONFIGURATION_KEY)).andAnswer(eval("false"));
        servletContext.setAttribute(eq(ServletUtils.VELOCITY_VIEW_KEY), anyObject(VelocityView.class));
    }

    protected void recordFilterConfig() throws Exception
    {
        recordFilterConfig(false, false, false);
    }

    protected void recordFilterConfig(boolean redirectTowardsLogin) throws Exception
    {
        recordFilterConfig(redirectTowardsLogin, false, false);
    }

    protected void recordFilterConfig(boolean redirectTowardsLogin, boolean redirectGetRequests, boolean forwardPostRequests) throws Exception
    {
        expect(filterConfig.getServletContext()).andAnswer(eval(servletContext)).anyTimes();
        expect(filterConfig.getInitParameter(WebappModelProvider.MODALITY_USER_CONFIG_KEY)).andAnswer(eval(null));
        expect(servletContext.getInitParameter(WebappModelProvider.MODALITY_USER_CONFIG_KEY)).andAnswer(eval(null));
        expect(servletContext.getResourceAsStream("/WEB-INF/modality.properties")).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(WebappModelProvider.MODEL_ID)).andAnswer(eval(null));
        expect(servletContext.getInitParameter(WebappModelProvider.MODEL_ID)).andAnswer(eval(null));
        expect(filterConfig.getInitParameter(BaseAuthFilter.REALM)).andAnswer(eval("TESTS"));
        expect(filterConfig.getInitParameter(BaseAuthFilter.PROTECTED_RESOURCES)).andAnswer(eval(".*"));
        expect(filterConfig.getInitParameter(BaseAuthFilter.PUBLIC_RESOURCES)).andAnswer(eval(null));
        expect(servletContext.getInitParameter(BaseAuthFilter.PUBLIC_RESOURCES)).andAnswer(eval(null));
        expect(servletContext.getContextPath()).andReturn("/");
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.MAX_INACTIVE_INTERVAL)).andAnswer(eval("0"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.LOGGED_SESSION_KEY)).andAnswer(eval("_user_"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.DOLOGIN_URI)).andAnswer(eval("/login.do"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.DOLOGOUT_URI)).andAnswer(eval("/logout.do"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.REDIRECT_PARAMETER)).andAnswer(eval("redirect"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.REDIRECT_REFERRER)).andAnswer(eval("true"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.REDIRECT_SKIP_CHECKS)).andAnswer(eval("false"));
        expect(filterConfig.getInitParameter(BaseSessionAuthFilter.INVALIDATE_ON_LOGOUT)).andAnswer(eval("true"));
        expect(servletContext.getContextPath()).andReturn("/");
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.LOGIN_FIELD)).andAnswer(eval("login"));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.PASSWORD_FIELD)).andAnswer(eval("password"));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.LOGIN_URI)).andAnswer(eval("/login.vhtml"));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.HOME_URI)).andAnswer(eval("/index.vhtml"));
        expect(servletContext.getResourcePaths("/")).andAnswer(eval(new HashSet<String>(Arrays.asList("/index.vhtml"))));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.USER_HOME_URI)).andAnswer(eval("/index.vhtml"));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.REDIRECT_TOWARDS_LOGIN)).andAnswer(eval(String.valueOf(redirectTowardsLogin)));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.REDIRECT_GET_ON_SUCCESS)).andAnswer(eval(String.valueOf(redirectGetRequests)));
        expect(filterConfig.getInitParameter(BaseFormAuthFilter.FORWARD_POST_ON_SUCCESS)).andAnswer(eval(String.valueOf(forwardPostRequests)));
        expect(filterConfig.getInitParameter(FormAuthFilter.USER_BY_CRED_ATTRIBUTE)).andAnswer(eval("user_by_credentials"));
    }

    protected void recordFilterRequireInit() throws Exception
    {
        expect(filterConfig.getInitParameter(ServletUtils.SHARED_CONFIG_PARAM)).andAnswer(eval("true"));
        expect(servletContext.getAttribute(ServletUtils.VELOCITY_VIEW_KEY)).andAnswer(() -> velocityView);
        expect(servletContext.getResource("/WEB-INF/modality.properties")).andAnswer(eval(null));
        expect(servletContext.getResource("/WEB-INF/model.properties")).andAnswer(eval(null));
        // expect(filterConfig.getInitParameter(ServletUtils.SHARED_CONFIG_PARAM)).andAnswer(eval("true"));
        // expect(servletContext.getAttribute(ServletUtils.VELOCITY_VIEW_KEY)).andAnswer(() -> velocityView);
        // Capture<Model> modelCapture = new Capture<>();
        // servletContext.setAttribute(eq("_MODEL_model"), capture(modelCapture));
        // expect(servletContext.getAttribute("_MODEL_model")).andAnswer(eval(null));
        expect(servletContext.getResource("/WEB-INF/model.xml")).andAnswer(eval(null));
    }

    protected Capture<Instance> recordSuccessfullLogin() throws Exception
    {
        expect(request.getRequestURI()).andAnswer(eval("/login.do"));
        expect(request.getSession(false)).andAnswer(eval(null));
        expect(request.getRequestURI()).andAnswer(eval("/login.do"));
        expect(request.getMethod()).andAnswer(eval("POST"));
        expect(request.getParameter("login")).andAnswer(eval("nestor"));
        expect(request.getParameter("password")).andAnswer(eval("secret"));
        expect(request.getSession()).andAnswer(eval(session));
        expect(session.isNew()).andAnswer(eval(true));
        Capture<Instance> user = new Capture<>();
        session.setAttribute(eq("_user_"), capture(user));
        session.setMaxInactiveInterval(0);
        return user;
    }

    private final Enumeration<String> emptyEnumeration = new Enumeration<String>()
    {
        @Override
        public boolean hasMoreElements()
        {
            return false;
        }

        @Override
        public String nextElement()
        {
            return null;
        }
    };

    protected Capture<SavedRequest> recordGETRequestCapture(String uri) throws Exception
    {
        expect(request.getMethod()).andAnswer(eval("GET"));
        expect(request.getScheme()).andAnswer(eval("http"));
        expect(request.getServerName()).andAnswer(eval("localhost"));
        expect(request.getServerPort()).andAnswer(eval(8080));
        expect(request.getRequestURI()).andAnswer(eval(uri));
        expect(request.getQueryString()).andAnswer(eval(null));
        expect(request.getHeaderNames()).andAnswer(eval(emptyEnumeration));
        expect(request.getContentType()).andAnswer(eval("text/html"));
        expect(request.getCharacterEncoding()).andAnswer(eval("utf-8"));
        expect(request.getAttributeNames()).andAnswer(eval(emptyEnumeration));
        expect(request.getSession()).andAnswer(eval(session));
        Capture<SavedRequest> savedRequest = new Capture<SavedRequest>();
        session.setAttribute(eq("org.apache.velocity.tools.auth.form.saved_request"), capture(savedRequest));
        return savedRequest;
    }

    protected Capture<SavedRequest> recordPOSTRequestCapture(String uri) throws Exception
    {
        expect(request.getMethod()).andAnswer(eval("POST"));
        expect(request.getScheme()).andAnswer(eval("http"));
        expect(request.getServerName()).andAnswer(eval("localhost"));
        expect(request.getServerPort()).andAnswer(eval(8080));
        expect(request.getRequestURI()).andAnswer(eval(uri));
        expect(request.getQueryString()).andAnswer(eval(null));
        expect(request.getHeaderNames()).andAnswer(eval(emptyEnumeration));
        expect(request.getContentType()).andAnswer(eval("application/x-www-form-urlencoded"));
        expect(request.getCharacterEncoding()).andAnswer(eval("utf-8"));
        expect(request.getAttributeNames()).andAnswer(eval(emptyEnumeration));
        expect(request.getContextPath()).andAnswer(eval(""));
        expect(request.getServletPath()).andAnswer(eval(uri));
        expect(request.getPathInfo()).andAnswer(eval(null));
        expect(request.getPathTranslated()).andAnswer(eval(null));
        expect(request.getInputStream()).andAnswer(eval(null));
        expect(request.getSession()).andAnswer(eval(session));
        Capture<SavedRequest> savedRequest = new Capture<SavedRequest>();
        session.setAttribute(eq("org.apache.velocity.tools.auth.form.saved_request"), capture(savedRequest));
        return savedRequest;
    }

}
