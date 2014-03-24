package net.sourceforge.stripes.examples;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.controller.DispatcherServlet;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.examples.bugzooky.bean.Person;
import net.sourceforge.stripes.examples.bugzooky.manager.PersonManager;
import net.sourceforge.stripes.mock.MockHttpSession;
import net.sourceforge.stripes.mock.MockRoundtrip;
import net.sourceforge.stripes.mock.MockServletContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class RoundTripTestBase<ActionClass extends ActionBean> {

    private static final Logger log = LoggerFactory.getLogger(RoundTripTestBase.class);

    protected static MockServletContext servletContext;
    protected final Person defaultUser = new PersonManager().getPerson(2);

    protected Class<ActionClass> actionClassType;
    protected ActionClass action;
    protected MockRoundtrip trip;
    protected MockHttpSession session;

    protected final BeanTestHelper beanTestHelper = new BeanTestHelper();

    @SuppressWarnings("unchecked")
    public RoundTripTestBase() {
        actionClassType = ((Class<ActionClass>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @BeforeClass
    public static void setup() {

        // when running multiple classes we need to make sure they do not reinitialize the shared code.
        if (servletContext == null) {

            log.debug("servlet context not initialized - creating it now");

            servletContext = spy(new MockServletContext(""));

            final Map<String, String> params = new HashMap<String, String>();
            final String basePackage = "net.sourceforge.stripes.examples";
            params.put("Configuration.Class", basePackage + ".MockConfiguration");
            params.put("Extension.Packages", basePackage + ".bugzooky.ext");
            params.put("ActionResolver.Packages", basePackage);
            params.put("PopulationStrategy.Class", "net.sourceforge.stripes.tag.BeanFirstPopulationStrategy");
            params.put("ActionResolver.Class", basePackage + ".MockActionResolver");
            servletContext.addFilter(StripesFilter.class, "StripesFilter", params);

            servletContext.setServlet(DispatcherServlet.class, "StripesDispatcher", null);

        } else {
            log.debug("using already created servlet context");
        }

    }

    /* Helper Methods */
    public static MockConfiguration getConfiguration() {
        return (MockConfiguration) StripesFilter.getConfiguration();
    }

    public static MockActionResolver getActionResolver() {
        return (MockActionResolver) getConfiguration().getActionResolver();
    }

    @SuppressWarnings({"unchecked"})
    @Before
    public void beforeRoundTripTestBase() throws InvocationTargetException, IllegalAccessException, InstantiationException {

        initMocks(this);

        final Field[] fields = this.getClass().getDeclaredFields();
        final Map<Class, Object> fieldMap = new HashMap<Class, Object>();
        for (final Field field : fields) {
            field.setAccessible(true); // screw you, access modifiers!
            fieldMap.put(field.getType(), field.get(this));
        }

        // get the constructor for the action we are testing (assumption - one constructor)
        final Constructor constructor = actionClassType.getConstructors()[0];

        // build the array for the constructor parameters
        final Class[] constructorTypes = constructor.getParameterTypes();
        final Object[] constructorParams = new Object[constructorTypes.length];
        for (int i = 0; i < constructorParams.length; i++) {
            constructorParams[i] = fieldMap.get(constructorTypes[i]);
        }

        // create the action
        action = (ActionClass) constructor.newInstance(constructorParams);

        prepareMockRoundTrip(action);

    }

    @After
    public void cleanup() {
        getActionResolver().cleanup();
    }

    protected void prepareMockRoundTrip(ActionBean actionBean) {

        // add our mock to the resolver
        getActionResolver().addMock(actionBean);

        // create a new session
        session = new MockHttpSession(servletContext);

        // create the trip
        trip = new MockRoundtrip(servletContext, actionBean.getClass(), session);

        // add the session to the request
        trip.getRequest().setSession(session);

    }

    protected String getRedirectForAction() {
        return getActionResolver().getUrlBinding(actionClassType);
    }

    protected String getRedirectForAction(final Class<? extends ActionBean> actionClass) {
        return getActionResolver().getUrlBinding(actionClass);
    }

    protected void setDefaultUser() {
        setUser(defaultUser);
    }

    protected void setUser(final Person person) {
        session.setAttribute("user", person);
    }

}

