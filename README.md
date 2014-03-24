Running
===
Run these maven goals:

    $ mvn clean compile war:war

That should build a war file that has all the parts where they need to be.

The one oddity here is that since this is a sample application, we include the source in the web app. In real life, I
wouldn't recommend doing that. In fact, I'd probably freak out if you did that on my project. But this is a sample, so
why not?

Differences from stripes-vanilla bugzooky
===

This uses spring to help manage the actions, so instead of creating new instances of the manager classes for bugs,
people, and components, they are injected in the constructors.

