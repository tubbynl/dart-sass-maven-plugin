package com.github.cleydyr.dart.command.factory;

import com.github.cleydyr.dart.command.AbstractSassCommand;
import com.github.cleydyr.dart.command.builder.AbstractSassCommandBuilder;
import com.github.cleydyr.dart.command.builder.SassCommandBuilder;
import com.github.cleydyr.dart.command.exception.SassCommandException;
import com.github.cleydyr.dart.release.DartSassReleaseParameter;
import com.github.cleydyr.dart.system.OSDetector;
import com.github.cleydyr.dart.system.io.utils.SystemUtils;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class OSDependentSassCommandBuilderFactory implements SassCommandBuilderFactory {
    @Override
    public SassCommandBuilder getCommanderBuilder() {
        return new AbstractSassCommandBuilder() {
            @Override
            protected AbstractSassCommand getSassCommandInstance(DartSassReleaseParameter dartSassReleaseParameter)
                    throws SassCommandException {
                String tmpDir = System.getProperty("java.io.tmpdir");
                if (tmpDir == null) {
                    throw new SassCommandException("java.io.tmpdir variable must be set");
                }

                Path tmpDirPath = SystemUtils.getExecutableTempFolder(dartSassReleaseParameter);
                if (!tmpDirPath.toFile().isDirectory()) {
                    throw new SassCommandException(tmpDirPath+" is not a valid directory");
                }

                Path sassExecutablePath = tmpDirPath.resolve(OSDetector.isWindows() ? "sass.bat" : "sass");
                if (!sassExecutablePath.toFile().canExecute()) {
                    throw new SassCommandException(sassExecutablePath+" is not executable");
                }

                Path dartExecutablePath = tmpDirPath.resolve("src").resolve(OSDetector.isWindows() ? "dart.exe" : "dart");
                if (!dartExecutablePath.toFile().canExecute()) {
                    throw new SassCommandException(dartExecutablePath+" is not executable");
                }

                return new AbstractSassCommand() {
                    @Override
                    protected void setExecutable(List<String> commands) {
                        commands.add(sassExecutablePath.toString());
                    }
                };
            }
        };
    }
}
