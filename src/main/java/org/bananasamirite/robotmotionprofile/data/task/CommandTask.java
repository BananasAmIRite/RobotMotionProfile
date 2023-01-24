package org.bananasamirite.robotmotionprofile.data.task;

import java.util.Arrays;
import java.util.List;

public class CommandTask extends TrajectoryTask {
    private String name;
    private List<Object> parameters;

    public CommandTask() {}

    public CommandTask(String name, Object... parameters) {
        this.name = name;
        this.parameters = Arrays.asList(parameters);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
}
