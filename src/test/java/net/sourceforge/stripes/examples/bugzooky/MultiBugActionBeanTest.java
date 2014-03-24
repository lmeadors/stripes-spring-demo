package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.examples.RoundTripTestBase;
import net.sourceforge.stripes.examples.bugzooky.biz.*;
import net.sourceforge.stripes.mock.MockRoundtrip;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MultiBugActionBeanTest extends RoundTripTestBase<MultiBugActionBean> {

    private final Person user = new PersonManager().getPerson(2);

    @Before
    public void beforeMultiBugActionBeanTest() {
        // all tests assume we're logged in
        setDefaultUser();
    }

    @Test
    public void should_go_to_bulk_add_if_view_not_set() throws Exception {

        // setup test

        // run test
        trip.execute();

        // verify outcomes

        // should go to edit page
        assertEquals(MultiBugActionBean.BUGZOOKY_BULK_ADD_EDIT, trip.getDestination());

        // should have 5 bugs in the list
        assertEquals(5, action.getBugs().size());

        // should have people and components set
        assertNotNull(action.getAllPeople());
        assertNotNull(action.getAllComponents());

        // we're not coming from a form
        assertFalse(action.isFromForm());

    }

    @Test
    public void should_go_back_to_caller_on_view_if_nothing_selected() throws Exception {

        // setup test
        trip.setParameter("view", "Bulk Edit");

        // run test
        trip.execute();

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());

    }

    @Test
    public void should_save_bugs() throws Exception {
        // setup test
        final Component component = new ComponentManager().getComponent(3);

        final Bug expected = new Bug();
        expected.setId(5);
        expected.setShortDescription("short desc");
        expected.setLongDescription("this has to be at least 25 characters long");
        expected.setComponent(component);
        expected.setOwner(user);
        expected.setPriority(Priority.Low);

        trip.setParameter("bugs[0]", expected.getId().toString());
        trip.setParameter("bugs[0].shortDescription", expected.getShortDescription());
        trip.setParameter("bugs[0].longDescription", expected.getLongDescription());
        trip.setParameter("bugs[0].component", expected.getComponent().getId().toString());
        trip.setParameter("bugs[0].owner", expected.getOwner().getId().toString());
        trip.setParameter("bugs[0].priority", expected.getPriority().name());

        // run test
        trip.execute("save");

        // verify outcome
        // the bug manager is as close to a mock as we'll get...
        final Bug actual = new BugManager().getBug(5);
        beanTestHelper.assertPropertiesAreEqual(expected, actual, "component", "owner");
        beanTestHelper.assertPropertiesAreEqual(expected.getComponent(), actual.getComponent());
        beanTestHelper.assertPropertiesAreEqual(expected.getOwner(), actual.getOwner());

        // where to next?
        assertEquals(getRedirectForAction(BugListActionBean.class), trip.getDestination());

    }

    @Test
    public void should_edit_selected_bugs() throws Exception {

        // setup test
        trip.setParameter("bugs[0]", "1");

        // run test
        trip.execute("view");

        // verify outcome
        assertEquals(MultiBugActionBean.BUGZOOKY_BULK_ADD_EDIT, trip.getDestination());

        final Bug actual = new BugManager().getBug(1);
        assertEquals(1, action.getBugs().size());
        beanTestHelper.assertPropertiesAreEqual(actual, action.getBugs().get(0));

    }

}
