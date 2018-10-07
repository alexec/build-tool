package bt.tasks.java.packaging;

import bt.api.Context;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.ModuleFound;

import javax.inject.Inject;

public class DeployJars implements Task {
    @Inject private Context context;

    @Subscribe
    public void moduleFound(ModuleFound jarCreated) {
        context.register(new DeployJar(jarCreated.getModule()));
    }
}
