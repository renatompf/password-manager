package io.renatofreire.passwordmanager.service;

import io.renatofreire.passwordmanager.dto.request.GeneratePasswordRequestDTO;
import io.renatofreire.passwordmanager.exception.BodyIsMissingException;
import io.renatofreire.passwordmanager.exception.InvalidFieldException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PasswordGeneratorService {
    private final char[] upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final char[] lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private final char[] specialCharacters = "@#$€%&({[]})<>".toCharArray();
    private final char[] numbers = "1234567890".toCharArray();
    private final SecureRandom random;

    public PasswordGeneratorService() {
        SecureRandom tempRandom;
        try {
            tempRandom = SecureRandom.getInstance("NativePRNGBlocking");
        } catch (NoSuchAlgorithmException e) {
            tempRandom = new SecureRandom();
        }
        random = tempRandom;
    }

    public String generatePassword(final GeneratePasswordRequestDTO passwordFields) throws BodyIsMissingException, InvalidFieldException {
        StringBuilder password = new StringBuilder();
        int passwordStartPosition = 0;
        Set<char[]> charCategoryToBeUsed = new HashSet<>();

        if(passwordFields == null){
            throw new BodyIsMissingException("Body to generate new password is missing");
        }

        if(passwordFields.length() <= 0){
            throw new InvalidFieldException("Length cannot be negative");
        }

        if(passwordFields.length() > 128){
            throw new InvalidFieldException("Length cannot be longer than 128");
        }

        if(passwordFields.useNumber()){
            charCategoryToBeUsed.add(numbers);
            password.append(numbers[random.nextInt(numbers.length)]);
            passwordStartPosition++;
        }

        if(passwordFields.useSpecials()){
            charCategoryToBeUsed.add(specialCharacters);
            password.append(specialCharacters[random.nextInt(specialCharacters.length)]);
            passwordStartPosition++;
        }

        if(passwordFields.useUpper()){
            charCategoryToBeUsed.add(upperCaseLetters);
            password.append(upperCaseLetters[random.nextInt(upperCaseLetters.length)]);
            passwordStartPosition++;
        }

        if(passwordFields.useLower()) {
            charCategoryToBeUsed.add(lowerCaseLetters);
            password.append(lowerCaseLetters[random.nextInt(lowerCaseLetters.length)]);
            passwordStartPosition++;
        }


        for(int i = passwordStartPosition; i < passwordFields.length(); i++) {
            char[] charCategory
                    = (char[]) charCategoryToBeUsed.toArray()[random.nextInt(charCategoryToBeUsed.size())];
            password.append(charCategory[random.nextInt(charCategory.length)]);
        }

        return shufflePassword(password.toString());

    }

    private String shufflePassword(final String passwordToShuffle) {
        List<Character> passwordChars = new ArrayList<>();
        for (char c : passwordToShuffle.toCharArray()) {
            passwordChars.add(c);
        }

        StringBuilder finalPassword = new StringBuilder(passwordToShuffle.length());
        while (!passwordChars.isEmpty()) {
            int randomIndex = random.nextInt(passwordChars.size());
            finalPassword.append(passwordChars.remove(randomIndex));
        }
        return finalPassword.toString();

    }

}
