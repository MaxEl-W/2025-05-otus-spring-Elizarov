package ru.otus.hw.shell;

import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@AllArgsConstructor
public class TestStarterShell {
    private final TestRunnerService testRunnerService;

    @ShellMethod("Start")
    public void start(@ShellOption String name) {
        testRunnerService.run();
    }
}
