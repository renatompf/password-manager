package io.renatofreire.passwordmanager.dto.response;

import com.nulabinc.zxcvbn.Feedback;
import io.renatofreire.passwordmanager.enums.StrengthScore;

public record PasswordStrengthDTO(
        String timeToCrackIt,
        StrengthScore score,
        Feedback feedback
) {
}
