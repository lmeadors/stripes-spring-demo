package net.sourceforge.stripes.examples;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.NameBasedActionResolver;

import java.util.HashMap;
import java.util.Map;

public class MockActionResolver extends NameBasedActionResolver {

    final Map<Class<? extends ActionBean>, ActionBean> mockActionBeans = new HashMap<Class<? extends ActionBean>, ActionBean>();

    public void addMock(ActionBean actionBean) {
        final Class<? extends ActionBean> actionClass = actionBean.getClass();
        this.mockActionBeans.put(actionClass, actionBean);
        super.addActionBean(actionClass);
    }

    @Override
    protected ActionBean makeNewActionBean(Class<? extends ActionBean> type, ActionBeanContext context) throws Exception {

        final ActionBean newActionBean;

        if (mockActionBeans.containsKey(type)) {
            newActionBean = mockActionBeans.get(type);
        } else {
            newActionBean = super.makeNewActionBean(type, context);
        }

        return newActionBean;

    }

    public void cleanup() {
        mockActionBeans.clear();
    }

}
