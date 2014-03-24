package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.examples.RoundTripTestBase;
import net.sourceforge.stripes.examples.bugzooky.manager.ComponentManager;
import net.sourceforge.stripes.examples.bugzooky.ext.BugzookyActionBeanContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdministerComponentsActionBeanTest extends RoundTripTestBase<AdministerComponentsActionBean> {

    @Before
    public void beforeAdministerComponentsActionBeanTest() {
        setDefaultUser();
    }

    @Test
    public void should_populate_component_list_for_view() throws Exception {

        // setup test - none required

        // run test
        trip.execute();

        // verify outcome
        assertNotNull(action.getComponents());
        assertEquals(AdministerComponentsActionBean.ADMINISTER_BUGZOOKY_JSP, trip.getDestination());

    }

    @Test
    public void should_get_component_list_as_view_helper() {

        // setup test
        action.setContext(new BugzookyActionBeanContext());

        // run test

        // verify outcome
        assertNotNull(action.getComponents());

    }

    @Test
    public void should_save_and_delete_components() throws Exception {

        // setup test
        trip.setParameter("deleteIds", "0");
        trip.setParameter("components[0].name", "test component");

        final ComponentManager componentManager = new ComponentManager();
        assertNotNull(componentManager.getComponent(0));

        // run test
        trip.execute("save");

        // verify outcome
        assertNull(componentManager.getComponent(0));

    }

}
