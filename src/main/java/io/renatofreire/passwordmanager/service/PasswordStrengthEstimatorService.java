package io.renatofreire.passwordmanager.service;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import io.renatofreire.passwordmanager.dto.request.StrengthPasswordInDTO;
import io.renatofreire.passwordmanager.dto.response.PasswordStrengthDTO;
import io.renatofreire.passwordmanager.enums.StrengthScore;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import org.springframework.stereotype.Service;

@Service
public class PasswordStrengthEstimatorService {

    private final Zxcvbn zxcvbn = new Zxcvbn();

    public PasswordStrengthDTO measurePasswordStrength(StrengthPasswordInDTO password){

        if(password.password() == null){
            throw new InvalidFieldException("Password cannot be null");
        }

        Strength measure = zxcvbn.measure(password.password());
        return new PasswordStrengthDTO(measure.getCrackTimesDisplay().getOnlineNoThrottling10perSecond(),
                StrengthScore.returnLabelByScore(measure.getScore()),
                measure.getFeedback());
    }

}
