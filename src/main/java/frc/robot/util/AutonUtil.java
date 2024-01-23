package frc.robot.util;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.Swerve.DrivetrainConstants;
import frc.robot.subsystems.Swerve.SwerveDrivetrain;
import frc.robot.util.Localization.SteelTalonsLocalization;

public class AutonUtil extends SubsystemBase {
    public AutonUtil() {
        AutoBuilder.configureHolonomic(
            SteelTalonsLocalization.getInstance()::getPose, 
            SteelTalonsLocalization.getInstance()::resetPose, 
            SwerveDrivetrain.getInstance()::getVelocityVector, 
            SwerveDrivetrain.getInstance()::setSpeedsAuton, 
            new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
                new PIDConstants(10.0, 0.0, 0.0), // Translation PID constants
                new PIDConstants(8.0, 0.0, 0.0), // Rotation PID constants
                DrivetrainConstants.MAX_PHYSICAL_SPEED_M_S, // Max module speed, in m/s
                Math.hypot(DrivetrainConstants.TRACKWIDTH, DrivetrainConstants.WHEELBASE)/2.0, // Drive base radius in meters. Distance from robot center to furthest module.
                new ReplanningConfig() // Default path replanning config. See the API for the options here
            ), 
            () -> {
                return !MiscUtil.isBlue();
            },
            SwerveDrivetrain.getInstance()
        );

    }

    public static void registerCommands() {

    }
}
