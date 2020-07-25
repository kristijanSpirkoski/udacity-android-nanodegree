package com.example.libjokeprovider;

public class JokeProvider {

    private int jokeCounter;
    private String[] jokes = {

            "What do you call a fake noodle? An Impasta.",
    "I would avoid the sushi if I was you. Its a little fishy.",
    "Want to hear a joke about paper? Nevermind its tearable.",
    "Why did the cookie cry? Because his father was a wafer so long!",
    "I used to work in a shoe recycling shop. It was sole destroying.",
    "What do you call a belt with a watch on it? A waist of time.",
    "How do you organize an outer space party? You planet.",
    "I went to a seafood disco last week... and pulled a mussel.",
    "Do you know where you can get chicken broth in bulk? The stock market.",
    "I cut my finger chopping cheese, but I think that I may have greater problems.",
    "My cat was just sick on the carpet, I dont think its feline well.",
    "Why did the octopus beat the shark in a fight? Because it was well armed.",
    "How much does a hipster weigh? An instagram.",
    "What did daddy spider say to baby spider? You spend too much time on the web.",
    "Atheism is a non-prophet organisation.",
    "Theres a new type of broom out, its sweeping the nation.",
    "What cheese can never be yours? Nacho cheese.",
    "What did the Buffalo say to his little boy when he dropped him off at school? Bison.",
    "Have you ever heard of a music group called Cellophane? They mostly wrap.",
    "Why does Superman gets invited to dinners? Because he is a Supperhero.",
    "How was Rome split in two? With a pair of Ceasars.",
    "The shovel was a ground breaking invention.",
    "A scarecrow says, \"This job isnt for everyone, but hay, its in my jeans.\"",
    "A Buddhist walks up to a hot dog stand and says, \"Make me one with everything.\"",
    "Did you hear about the guy who lost the left side of his body? Hes alright now.",
    "What do you call a girl with one leg thats shorter than the other? Ilene.",
    "I did a theatrical performance on puns. It was a play on words.",
    "What do you do with a dead chemist? You barium.",
    "I bet the person who created the door knocker won a Nobel prize.",
    "Towels cant tell jokes. They have a dry sense of humor.",
    "Two birds are sitting on a perch and one says \"Do you smell fish?\"",
    "Do you know sign language? You should learn it, its pretty handy.",
            "What do you call a beautiful pumpkin? GOURDgeous.",
    "Why did one banana spy on the other? Because she was appealing.",
    "What do you call a cow with no legs? Ground beef.",
    "What do you call a cow with two legs? Lean beef.",
    "What do you call a cow with all of its legs? High steaks.",
    "A cross eyed teacher couldnt control his pupils.",
    "After the accident, the juggler didnt have the balls to do it.",
    "I used to be afraid of hurdles, but I got over it.",
};

    public JokeProvider() {
        jokeCounter = 0;
    }

    public String giveMeAJoke() {
        String joke = jokes[jokeCounter];
        jokeCounter = (jokeCounter + 1) % jokes.length;
        return joke;
    }
}