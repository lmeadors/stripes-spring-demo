package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.examples.RoundTripTestBase;
import net.sourceforge.stripes.mock.MockRoundtrip;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LoginActionBeanTest extends RoundTripTestBase<LoginActionBean> {

    @Test
    public void should_go_to_view_page() throws Exception {

        // setup test

        // run test
        trip.execute();

        // verify outcome
        assertEquals(LoginActionBean.LOGIN_JSP, trip.getDestination());

    }

    @Test
    public void should_fail_without_username_or_password() throws Exception {

        // setup test

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());
        assertNull(action.getUsername());
        assertNull(action.getPassword());

    }

    @Test
    public void should_fail_without_username() throws Exception {

        // setup test
        trip.setParameter("password", "test_pass");

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());
        assertNull(action.getUsername());
        assertNotNull(action.getPassword());

    }

    @Test
    public void should_fail_without_password() throws Exception {

        // setup test
        trip.setParameter("username", "test_user");

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());
        assertNotNull(action.getUsername());
        assertNull(action.getPassword());

    }

    @Test
    public void should_fail_if_unknown_user() throws Exception {

        // setup test
        trip.setParameter("username", "test_user");
        trip.setParameter("password", "test_pass");

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());
        assertNotNull(action.getUsername());
        assertNotNull(action.getPassword());

    }

    @Test
    public void should_fail_if_bad_password() throws Exception {

        // setup test
        trip.setParameter("username", "fred");// a real user
        trip.setParameter("password", "test_pass");

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());
        assertNotNull(action.getUsername());
        assertNotNull(action.getPassword());

    }

    @Test
    public void should_save_user() throws Exception {

        // setup test
        trip.setParameter("username", "fred");// a real user
        trip.setParameter("password", "fred");// actual password

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(getRedirectForAction(BugListActionBean.class), trip.getDestination());
        assertNotNull(action.getUsername());
        assertNotNull(action.getPassword());

    }

    @Test
    public void should_save_user_and_go_to_forward_url() throws Exception {

        // setup test
        trip.setParameter("username", "fred");// a real user
        trip.setParameter("password", "fred");// actual password
        final String targetUrl = "http://www.test.com";
        trip.setParameter("targetUrl", targetUrl);

        // run test
        trip.execute("login");

        // verify outcome
        assertEquals(targetUrl, trip.getDestination());
        assertNotNull(action.getUsername());
        assertNotNull(action.getPassword());
        assertNotNull(action.getTargetUrl());

    }

}
