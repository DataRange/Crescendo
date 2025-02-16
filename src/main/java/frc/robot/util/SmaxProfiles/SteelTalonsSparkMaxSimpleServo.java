package frc.robot.util.SmaxProfiles;

import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.SparkPIDController.ArbFFUnits;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.util.MiscUtil;
import frc.robot.util.STSmaxConfig;
import frc.robot.util.SteelTalonsLogger;

public class SteelTalonsSparkMaxSimpleServo {

    private CANSparkMax smax;
    private RelativeEncoder smaxEnc;
    private SparkPIDController smaxController;
    private STSmaxConfig config;
    private double setPoint = 0;

    public SteelTalonsSparkMaxSimpleServo(STSmaxConfig config) {
        this.config = config;
        smax = new CANSparkMax(config.id, MotorType.kBrushless);
        Timer.delay(0.15);
        smax.setInverted(config.inverted);
        smax.setSmartCurrentLimit(config.currentLimit);
        smax.setIdleMode(config.idleMode);
        smaxEnc = smax.getEncoder();
        smaxEnc.setMeasurementPeriod(10);
        double positionConv = config.isRotational ? (2 * Math.PI * config.gearing) : (config.gearing * config.finalDiameterMeters * Math.PI);
        //Rotational subsystem: Rad - Rad/s --- Linear subsystem: M - M/s
        smaxEnc.setPositionConversionFactor(positionConv);
        smaxEnc.setVelocityConversionFactor(positionConv / 60.0);
        smaxEnc.setPosition(0);
        smaxController = smax.getPIDController();
        smaxController.setP(config.kP);
        smaxController.setI(config.kI);
        smaxController.setD(config.kD);
        smaxController.setFF(config.kFF);
        if (config.isRotational) {
            smaxController.setPositionPIDWrappingEnabled(true);
            smaxController.setPositionPIDWrappingMaxInput(Math.PI);
            smaxController.setPositionPIDWrappingMinInput(-Math.PI);
        }
        MiscUtil.doPeriodicFrame(smax);
        smax.burnFlash();
        Timer.delay(0.15);
    }

    public void setRaw(double percent) {
        smax.set(percent);
    }

    public CANSparkMax getSmax() {
        return smax;
    }

    public void setSetpoint(double setPoint, double arbFFVolts) {
        this.setPoint = setPoint;
        smaxController.setReference(setPoint, ControlType.kPosition, 0, arbFFVolts, ArbFFUnits.kVoltage);
    }

    public double getSetPoint() {
        return setPoint;
    }

    public void forceStop() {
        smax.setVoltage(0);
    }

    public void disableContinuousInput() {
        smaxController.setPositionPIDWrappingEnabled(false);
    }

    public double getPosition() {
        return smaxEnc.getPosition();
    }

    public void setPosition(double pos) {
        smaxEnc.setPosition(pos);
    }

    public double getVelocity() {
        return smaxEnc.getVelocity();
    }

    public double getError() {
        if (config.isRotational) {
            return new Rotation2d(setPoint).minus(new Rotation2d(getPosition())).getRadians();
        } else {
            return setPoint - getPosition();
        }
    }

    public void resetController() {
        // System.err.println("empty method");
    }

    public void log() {
        String name = config.name;
        SteelTalonsLogger.post(name + ": Applied Output (%)", smax.getAppliedOutput());
        SteelTalonsLogger.post(name + ": Output Current (A)", smax.getOutputCurrent());
        SteelTalonsLogger.post(name + ": Temp (C)", smax.getMotorTemperature());
        SteelTalonsLogger.post(name + ": Is Braked? (Bool)", smax.getIdleMode().equals(IdleMode.kBrake));
        SteelTalonsLogger.post(name + ": Position (rad or Meters)", getPosition());
        SteelTalonsLogger.post(name + ": Velocity (rad/s or Meters/s)", getVelocity());
        SteelTalonsLogger.post(name + ": Setpoint (rad or Meters)", getSetPoint());
        SteelTalonsLogger.post(name + ": Error (rad or Meters)", getError());
    }
}
