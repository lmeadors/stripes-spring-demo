package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.examples.RoundTripTestBase;
import net.sourceforge.stripes.examples.bugzooky.bean.Person;
import net.sourceforge.stripes.examples.bugzooky.ext.BugzookyActionBeanContext;
import net.sourceforge.stripes.mock.MockRoundtrip;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ViewResourceActionBeanTest extends RoundTripTestBase<ViewResourceActionBean> {

    @Test
    public void should_not_work_for_anonymous_users() throws Exception {

        // setup test
        trip.setParameter("resource", "/WEB-INF/classes/Foo.class");

        // run test
        trip.execute("view");

        // verify outcome
        assertTrue(trip.getDestination().startsWith(getRedirectForAction(LoginActionBean.class)));

    }

    @Test
    public void should_work_for_authenticated_users() throws Exception {

        // setup test
        final String resource = "test.jsp";
        final String testContent = "test content";
        final String expected = "<html><head><title>" + resource + "</title></head><body><pre>" + testContent + "\n</pre></body></html>";
        when(servletContext.getResourceAsStream(resource)).thenReturn(new ByteArrayInputStream(testContent.getBytes("utf-8")));

        setUser(new Person("test", "test", "test", "test", "test@test.com"));

        trip.setParameter("resource", resource);

        // run test
        trip.execute("view");

        // verify outcome
        assertEquals(null, trip.getDestination());
        assertEquals(expected, trip.getOutputString());
        assertEquals(resource, action.getResource());

    }

    @Test
    public void should_not_allow_web_inf_files_not_in_src() throws Exception {

        // setup test
        final String resource = "/WEB-INF/something/test.jsp";
        setUser(new Person("test", "test", "test", "test", "test@test.com"));
        trip.setParameter("resource", resource);

        // run test
        trip.execute("view");

        // verify outcome
        assertEquals(MockRoundtrip.DEFAULT_SOURCE_PAGE, trip.getDestination());
        assertFalse(action.getContext().getValidationErrors().isEmpty());

    }

    @Test
    public void should_allow_web_inf_files_in_src() throws Exception {

        // setup test
        final String resource = "/WEB-INF/src/something/test.jsp";
        final String testContent = "test content";
        when(servletContext.getResourceAsStream(resource)).thenReturn(new ByteArrayInputStream(testContent.getBytes("utf-8")));
        setUser(new Person("test", "test", "test", "test", "test@test.com"));
        trip.setParameter("resource", resource);

        // run test
        trip.execute("view");

        // verify outcome
        assertEquals(null, trip.getDestination());
        assertTrue(action.getContext().getValidationErrors().isEmpty());

    }

    @Test
    public void get_resources_should_remove_non_java_and_jsp_files() {

        // setup test
        final BugzookyActionBeanContext mockContext = mock(BugzookyActionBeanContext.class);
        final ServletContext context = mock(ServletContext.class);
        when(mockContext.getServletContext()).thenReturn(context);
        List<String> resources = Arrays.asList("yes.jsp", "SomeClass.java", "ignore_me.properties");
        when(context.getResourcePaths("/bugzooky/")).thenReturn(new TreeSet<String>(resources));
        action.setContext(mockContext);

        // run test
        final Collection availableResources = action.getAvailableResources();

        // verify outcome
        assertEquals(2, availableResources.size());
        verify(context, times(3)).getResourcePaths(anyString());

    }

}
