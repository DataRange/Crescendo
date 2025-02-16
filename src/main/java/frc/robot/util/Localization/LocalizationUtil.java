package frc.robot.util.Localization;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import frc.robot.subsystems.Swerve.DrivetrainConstants;
import frc.robot.util.MiscUtil;

public class LocalizationUtil {
    public static Optional<SteelTalonsVisionMeasurement> findConfidence(Optional<EstimatedRobotPose> estimate, Pose2d refPose, Optional<Pose2d> lastPose) {
        if (estimate.isPresent()) {
            double timestamp = estimate.get().timestampSeconds;
            Pose2d pose = estimate.get().estimatedPose.toPose2d();
            double diffFromRef = pose.getTranslation().minus(refPose.getTranslation()).getNorm();
            // double diffFromLast = pose.getTranslation().minus(lastPose.get().getTranslation()).getNorm();

            boolean trustableA = 
                diffFromRef < DrivetrainConstants.MAX_TRANSLATION_SPEED_M_PER_LOOP ||
                MiscUtil.drivetrainSpeedMagnitude() < 1.0;

            boolean trustableB = lastPose.isPresent() ?
            pose.getTranslation().minus(lastPose.get().getTranslation()).getNorm() < DrivetrainConstants.MAX_TRANSLATION_SPEED_M_PER_LOOP :
                true;

            boolean trustableC = 
                pose.getX() <= MiscUtil.fieldWidth && 
                pose.getY() <= MiscUtil.fieldHeight;

            double stDev = trustableA && trustableB && trustableC ? 0.0 : Double.MAX_VALUE;
            Matrix<N3, N1> confidence = VecBuilder.fill(stDev, stDev, Double.MAX_VALUE); //FIXME may need to tune
            return Optional.of(new SteelTalonsVisionMeasurement(pose, confidence, timestamp));
        } else {
            return Optional.empty();
        }
    }
}
