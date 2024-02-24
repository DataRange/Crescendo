package frc.robot.subsystems.Shooter;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class FeedShooter extends Command {

    private Shooter shooter;
    private Timer timer;
    private Timer timer2;
    private final double timerThreshold = 0.25;
    private double setpoint = 0.0;
    private Rotation2d pivotRot;

    public FeedShooter(double setpoint, Rotation2d pivotRot) {
        shooter = Shooter.getInstance();
        timer = new Timer();
        timer2 = new Timer();

        this.setpoint = setpoint;
        this.pivotRot = pivotRot;

        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        timer.reset();
        timer2.reset();
        timer2.start();

        shooter.setPivotSetpoint(pivotRot);
    }

    @Override
    public void execute() {
        shooter.setFlywheelSetpoint(setpoint, setpoint);
        if (shooter.pivotAtGoal() && shooter.flywheelAtGoal() && timer2.get() > 0.25) {
            System.err.println("starting the flywheel");
            shooter.setFeederSetpoint(ShooterConstants.FEEDER_FEED_SPEED);
            timer.start();
        } else {
            timer.reset();
        }
    }

    @Override
    public boolean isFinished() {
        return timer.get() > timerThreshold && !shooter.loaded();
    }

    @Override
    public void end(boolean interrupted) {
        System.err.println("finishing fefed - interrupted: " + interrupted);
        shooter.setFeederSetpoint(ShooterConstants.FEEDER_HOLD_SPEED);
        shooter.setPivotSetpoint(ShooterConstants.SHOOTER_PIVOT_STOW);
        shooter.setFlywheelSetpoint(ShooterConstants.FLYWHEEL_STATIC_SPEED_RPM, ShooterConstants.FLYWHEEL_STATIC_SPEED_RPM);
        timer.stop();
        timer2.stop();
    }
}
