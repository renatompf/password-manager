package io.renatofreire.passwordmanager.service;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import io.renatofreire.passwordmanager.dto.response.PasswordStrengthDTO;
import io.renatofreire.passwordmanager.enums.StrengthScore;
import org.springframework.stereotype.Service;

@Service
public class PasswordStrengthEstimatorService {

    private final Zxcvbn zxcvbn = new Zxcvbn();

    public PasswordStrengthDTO measurePasswordStrength(String password){

        Strength measure = zxcvbn.measure(password);
        return new PasswordStrengthDTO(measure.getCrackTimesDisplay().getOnlineNoThrottling10perSecond(),
                StrengthScore.returnLabelByScore(measure.getScore()),
                measure.getFeedback());
    }

}
