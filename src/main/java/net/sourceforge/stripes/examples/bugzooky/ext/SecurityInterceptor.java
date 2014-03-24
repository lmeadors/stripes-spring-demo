/* Copyright 2009 Ben Gunter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.stripes.examples.bugzooky.ext;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.examples.bugzooky.LoginActionBean;
import net.sourceforge.stripes.examples.bugzooky.biz.Person;
import net.sourceforge.stripes.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * After the {@link LifecycleStage#ActionBeanResolution} stage, this interceptor checks the resolved
 * {@link ActionBean} class for a {@link Public} annotation. If none is present, then the client is
 * redirected to the login page.
 *
 * @author Ben Gunter
 */
@Intercepts(LifecycleStage.ActionBeanResolution)
public class SecurityInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);

    public Resolution intercept(final ExecutionContext context) throws Exception {

        final HttpServletRequest request = context.getActionBeanContext().getRequest();
        final String baseUrl = HttpUtil.getRequestedPath(request);

        final String url;

        if (request.getQueryString() != null) {
            url = baseUrl + '?' + request.getQueryString();
        } else {
            url = baseUrl;
        }

        log.debug("Intercepting request: {}", url);

        final Resolution finalResolution;
        final Resolution nextResolution = context.proceed();

        final Person user = ((BugzookyActionBeanContext) context.getActionBeanContext()).getUser();

        // A null resolution here indicates a normal flow to the next stage
        if (user == null) {
            if (nextResolution == null) {
                final ActionBean bean = context.getActionBean();
                if (bean != null && !bean.getClass().isAnnotationPresent(Public.class)) {
                    log.warn("Thwarted attempted to access {}", bean.getClass().getSimpleName());
                    finalResolution = new RedirectResolution(LoginActionBean.class).addParameter("targetUrl", url);
                } else {
                    log.debug("URL '{}' is public", baseUrl);
                    finalResolution = null;
                }
            }else {
                log.debug("no user, but something else replaced the resolution with {}", nextResolution);
                finalResolution = nextResolution;
            }
        } else {
            log.debug("User {} is authorized to URL '{}'", user.getUsername());
            finalResolution = nextResolution;
        }

        return finalResolution;

    }

}
