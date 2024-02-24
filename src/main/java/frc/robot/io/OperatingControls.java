package frc.robot.io;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.SubsystemManager;
import frc.robot.subsystems.Intake.Intake;
import frc.robot.subsystems.Shooter.Shooter;
import frc.robot.subsystems.Shooter.ShooterConstants;

public class OperatingControls {

    public OperatingControls(CommandXboxController operatingController) {
        operatingController.leftTrigger(0.1).whileTrue(
            SubsystemManager.getComplexIntakeCommand()
        );

        operatingController.rightTrigger(0.1).whileTrue(
            Intake.getInstance().getIntakeEjaculation()
        );

        operatingController.leftBumper().onTrue(Intake.getInstance().getHomingCommand());
        operatingController.rightBumper().onTrue(Shooter.getInstance().getHomingCommand());

        operatingController.a().onTrue(new ParallelCommandGroup(
            Shooter.getInstance().getShooterHandoff(), 
            Intake.getInstance().getIntakeHandoff()
        ));
        operatingController.y().onTrue(Shooter.getInstance().getFeedCommand(5200, ShooterConstants.SHOOTER_PIVOT_ACTIVE));
        operatingController.x().onTrue(Shooter.getInstance().getFeedCommand(1000, ShooterConstants.SHOOTER_PIVOT_AMP));

        // operatingController.start().toggleOnTrue(new TestShooterRanging());
        // operatingController.x().onTrue(new ShooterHandoff());
    }
}
