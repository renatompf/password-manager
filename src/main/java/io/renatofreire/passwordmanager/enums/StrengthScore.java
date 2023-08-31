package io.renatofreire.passwordmanager.enums;

import io.renatofreire.passwordmanager.exception.InvalidFieldException;

public enum StrengthScore {
    WEAK(0),
    FAIR(1),
    GOOD(2),
    STRONG(3),
    VERY_STRONG(4);

    private final Integer score;

    StrengthScore(Integer score) {
        this.score = score;
    }

    public static final StrengthScore[] values = values();

    public static StrengthScore returnLabelByScore(Integer score) {
        for (StrengthScore strengthScore : values) {
            if (strengthScore.score.equals(score)) {
                return strengthScore;
            }
        }
        throw new InvalidFieldException("Invalid score: " + score);
    }

}
