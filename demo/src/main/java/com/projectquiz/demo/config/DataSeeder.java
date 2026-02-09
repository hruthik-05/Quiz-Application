package com.projectquiz.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.projectquiz.demo.models.Difficulty;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.repositories.QuestionRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (questionRepository.count() == 0) {
            System.out.println("Seeding Questions...");
            

            createQuestion("Java", Difficulty.EASY, "What is the default value of int variable?", Arrays.asList("0", "0.0", "null", "undefined"), "0");
            createQuestion("Java", Difficulty.EASY, "Which keyword is used to define a class?", Arrays.asList("class", "struct", "define", "object"), "class");
            createQuestion("Java", Difficulty.MEDIUM, "Which of these is not a Java feature?", Arrays.asList("Object Oriented", "Use of pointers", "Portable", "Dynamic"), "Use of pointers");
            createQuestion("Java", Difficulty.MEDIUM, "What is the size of boolean variable?", Arrays.asList("8 bit", "16 bit", "32 bit", "not precisely defined"), "not precisely defined");
            createQuestion("Java", Difficulty.HARD, "Which class is the superclass of all classes?", Arrays.asList("Object", "Class", "System", "Runtime"), "Object");
            createQuestion("Java", Difficulty.HARD, "What is the return type of hashCode() method?", Arrays.asList("int", "long", "Object", "void"), "int");


            createQuestion("Python", Difficulty.EASY, "How do you print in Python?", Arrays.asList("echo", "print()", "System.out.println", "printf"), "print()");
            createQuestion("Python", Difficulty.MEDIUM, "Which is immutable?", Arrays.asList("List", "Dictionary", "Set", "Tuple"), "Tuple");
            createQuestion("Python", Difficulty.HARD, "What is the output of print(2**3)?", Arrays.asList("6", "8", "9", "Error"), "8");

            System.out.println("Seeding Complete.");
        }
    }

    private void createQuestion(String category, Difficulty difficulty, String text, List<String> options, String answer) {
        Question q = new Question();
        q.setCategory(category);
        q.setDifficulty(difficulty);
        q.setQuestionText(text);
        q.setOptions(options);
        q.setAnswer(answer);
        q.setPoints(difficulty == Difficulty.EASY ? 10 : difficulty == Difficulty.MEDIUM ? 20 : 30);
        questionRepository.save(q);
    }
}
