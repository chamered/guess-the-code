import java.util.*;

/*
    Student Code Ethics

    Students are expected to maintain the highest standards of
    academic integrity. Work that is not of the student's own
    creation will receive no credit. Remember that you cannot
    give or receive unauthorized aid on any assignment, quiz,
    or exam. A student cannot use the ideas of another and
    declare it as his or her own. Students are required to
    properly cite the original source of the ideas and
    information used in his or her work.
*/

/*
    ANSI Escape Codes for Text Color
    Reset: \u001B[0m
    Blue: \u001B[34m
    Cyan: \u001B[36m
    Red: \u001B[31m
    Green: \u001B[32m
    White: \u001B[37m
    Yellow: \u001B[33m
    Black: \u001B[30m
    Magenta: \u001B[35m
*/

/**
 * An interactive command line-based game where users can attempt to guess a secret code.
 * The game includes features such as revealing a parts of the secret code, displaying history
 * of guesses, and providing assistance to the player. The game has also one extra game mode:
 * - "ROULETTE MODE": The first three characters of the code are revealed and the user need to
 *                    choose between two options to guess the last character.
 */
public class GuessTheCode {
    // Random object for generating the secret code
    static Random random = new Random();

    // Scanner object to take player input
    static Scanner scanner = new Scanner(System.in);

    static final char[] SET_OF_CHAR = {'a', 'b', 'c', 'd', 'e', 'f'}; // Set of valid characters
    final static int CODE_LENGTH = 4; // Maximum length of the code

    static String code = generateCode(); // Secret code to guess
    static int attempts = 20; // Number of attempts for the player
    static String playerCode; // Player's current guess

    // Tracking history of guesses and evaluations
    static ArrayList<String> guessesHistory = new ArrayList<>();
    static ArrayList<String> evaluationsHistory = new ArrayList<>();
    static String evaluationGroup = "";

    static ArrayList<Integer> alreadyUsed = new ArrayList<>(); // List of all already revealed indexes of the code
    static char revealedChar; // Character to be revealed

    public static void main(String[] args) {
        boolean playAgain = true; // Indicates if the player wants to restart the game

        while (playAgain) {
            welcomeMessage(); // Display welcome message and commands
            System.out.print("\nProgrammed by Samuele Maltauro aka \u001B[31mChamered\u001B[0m");

            // Until the player has enough attempts
            while (attempts > 0) {
                System.out.print("\n" + attempts + ">"); // Prompt player with remaining attempts
                playerCode = scanner.nextLine(); // Takes player input
                playerCode = playerCode.replaceAll("\\s", ""); // Remove all the white spaces
                playerCode = playerCode.toLowerCase(); // Set the input in lower case

                // Store valid guesses history
                if (!isCommand(playerCode) && playerCode.length() == CODE_LENGTH) guessesHistory.add(playerCode);

                // Gives an error if the player input is invalid
                if (playerCode.length() != 4 && !isCommand(playerCode)) {
                    System.out.print("You must enter only a 4-character code!");
                    continue;
                }

                // Check for win condition
                if (playerCode.equals(code)) {
                    System.out.println("\u001B[34m*******************************************************************");
                    System.out.println("* CONGRATULATIONS! From now, the POWER OF \u001B[33mJUICE\u001B[34m will be with you! *");
                    System.out.println("*******************************************************************");
                    return;
                }

                // Check if the input is a command or a guess
                checkInput(playerCode);
            }
            System.out.println("pc: " + playerCode);

            // Handle game over scenario
            if (attempts == 0) {
                System.out.println("\n\u001B[31m**********************************************");
                System.out.println("* GAME OVER. Drink more \u001B[33mJUICE\u001B[31m and try again. *");
                System.out.println("**********************************************");
                System.out.println("\u001B[0mIf you wanna play again, type 'new', if you wanna exit, type 'quit'.");
                System.out.print(">");
                playerCode = scanner.next();
                playAgain = playerCode.equals("new");
                if (playAgain) newGame();
            }
        }
    }

    /**
     * Displays the welcome message, game rules and available commands.
     */
    public static void welcomeMessage() {
        System.out.println("\u001B[34m----------------------------------------------------------------------------");
        System.out.println("|                           WELCOME TO BzGuessGame!                        |");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("| Your goal is to guess a secret 4-character code.                         |");
        System.out.println("| The code uses letters a, b, c, d, e, and f, and may include duplicates.  |");
        System.out.println("| After each guess, you'll get feedback:                                   |");
        System.out.println("| - 'X' means a correct letter in the correct position.                    |");
        System.out.println("| - '-' means a correct letter in the wrong position.                      |");
        System.out.println("| You have 20 attempts to uncover the code. Good luck!                     |");
        System.out.println("--------------------------------------\u001B[32m--------------------------------------");
        System.out.println("|                            AVAILABLE COMMANDS                            |");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("| - 'new'  :  Start a new game                                             |");
        System.out.println("| - 'help' :  Show game rules and commands                                 |");
        System.out.println("| - 'buy'  :  Buy a letter (costs 5 attempts)                              |");
        System.out.println("| - 'rlt'  :  Enter in roulette game-mode                                  |");
        System.out.println("| - 'h'    :  Show guess and evaluations history                           |");
        System.out.println("| - 'clrh' :  Clear the history                                            |");
        System.out.println("| - 'quit' :  Exit the game                                                |");
        System.out.print("----------------------------------------------------------------------------\u001B[0m");
    }

    /**
     * Generates a random secret code of length CODE_LENGTH using character from SET_OF_CHAR.
     * @return the random generated code.
     */
    public static String generateCode() {
        code = "";
        for (int i = 1; i <= CODE_LENGTH; i++) {
            char randomChar = SET_OF_CHAR[random.nextInt(SET_OF_CHAR.length - 1)];
            code += randomChar;
        }
        return code;
    }

    /**
     * Evaluates the player's guess and gives feedback.
     * - 'X' for correct characters in the correct position.
     * - '-' for correct characters in the wrong position.
     */
    public static void evaluate() {
        StringBuilder codeCopy = new StringBuilder(code); // Duplicate of the code
        StringBuilder playerCodeCopy = new StringBuilder(playerCode); // Duplicate of the player guess

        evaluationGroup = ""; // Reset the evaluation group to store the new one

        // Check for the correct characters in the correct position
        for (int i = 0; i < code.length(); i++) {
            if (playerCode.charAt(i) == code.charAt(i)) {
                System.out.print("X");
                codeCopy.setCharAt(i, ' ');
                playerCodeCopy.setCharAt(i, ' ');
                evaluationGroup += "X";
            }
        }

        // Check for correct characters in wrong positions
        for (int i = 0; i < codeCopy.length(); i++) {
            char playerChar = playerCodeCopy.charAt(i);
            if (playerChar != ' ') {
                int index = codeCopy.indexOf(String.valueOf(playerChar));
                if (index != -1) {
                    System.out.print("-");
                    codeCopy.setCharAt(index, ' ');
                    evaluationGroup += "-";
                }
            }
        }

        evaluationsHistory.add(evaluationGroup); // Store evaluation feedback

        attempts--; // Decrease by one attempt
    }

    /**
     * Check a given input. If it is a command execute it, otherwise evaluate it.
     * @param input the input string to check.
     */
    public static void checkInput(String input) {
        switch (input) {
            case "new" -> newGame();
            case "help" -> welcomeMessage();
            case "buy" -> buyChar();
            case "rlt" -> enterRouletteMode();
            case "h" -> displayHistory();
            case "clrh" -> clearHistory();
            case "r" -> System.out.print(code); // Reveal the secret code (debug purpose only)
            case "p" -> code = scanner.nextLine(); // Preset the secret code (debug purpose only)
            case "quit" -> quitGame();
            default -> evaluate();
        }
    }

    /**
     * Checks if the given input string is a valid command.
     * @param input the input string to check.
     * @return True if the input is a command, false otherwise.
     */
    public static boolean isCommand(String input) {
        return switch (input) {
            case "new", "help", "buy", "rlt", "h", "clrh", "r", "p", "quit" -> true;
            default -> false;
        };
    }

    /**
     * Reset the game state and generates a new secret code.
     */
    public static void newGame() {
        System.out.println("\u001B[36mThe game has RESTARTED and a NEW SECRET CODE has been GENERATED.\u001B[0m");
        code = generateCode();
        attempts = 20;
        clearHistory();
    }

    /**
     * Reveals randomly one character of the secret code.
     */
    public static void buyChar() {
        // If the player hasn't enough attempts, display a message
        if (attempts <= 5) {
            System.out.print("You don't have enough attempts to buy a letter.");
            return;
        }

        System.out.print("This is the character revealed: ");

        int index; // Index at which there is the character to be revealed
        // Find the character to reveal until is different from the already revealed
        do {
            index = random.nextInt(4);
            revealedChar = code.charAt(index);
        } while (alreadyUsed.contains(index));
        alreadyUsed.add(index);

        // Prints the character to reveal at its right position
        for (int i = 0; i < 4; i++) {
            if (i == index) {
                System.out.print(revealedChar);
            } else {
                System.out.print(".");
            }
        }

        attempts -= 5; // Decrease by 5 attempts (costs of buy)
    }

    /**
     * Enters the roulette game mode.
     * A new secret code is generated and the first three character are revealed. The player has to guess
     * the last character by choosing between two characters and has one attempt to guess the right one.
     */
    public static void enterRouletteMode() {
        String char1; // First character to choose from
        String char2; // Second character to choose from
        // Generate a new code and pick another random character from the SET_OF_CHAR until the right character
        // and the other one are different
        do {
            generateCode();
            char1 = code.substring(3);
            char2 = String.valueOf(SET_OF_CHAR[random.nextInt(SET_OF_CHAR.length - 1)]);
        } while (char2.equals(char1));

        // Display the game mode info
        System.out.println("\u001B[31m|-------------- ROULETTE MODE ACTIVATED --------------|\u001B[0m");
        System.out.println("The first three characters of the secrete code are: \u001B[31m" + code.substring(0, 3) + "\u001B[0m");
        System.out.println("You have one attempt to guess the last character.");
        System.out.print("You need to choose between:\u001B[31m " +
            ((random.nextBoolean()) ? char1 + " | " + char2 : char2 + " | " + char1) + "\u001B[0m");

        attempts = 1; // Set the attempts to 1
    }

    /**
     * Displays the history of all guesses and their evaluations.
     */
    public static void displayHistory() {
        // If the history is empty, display a message
        if (guessesHistory.isEmpty()) {
            System.out.print("\u001B[33mThe history is empty.\u001B[0m");
            return;
        }

        System.out.println("History of all guesses and evaluations:\u001B[33m");

        // Display the history of each guess with its evaluation
        for (int i = 0; i < guessesHistory.size(); i++) {
            System.out.println("|> " + guessesHistory.get(i) + " " + evaluationsHistory.get(i));
        }

        System.out.print("\u001B[0m");
    }

    /**
     * Clears the history of guesses and evaluations.
     */
    public static void clearHistory() {
        guessesHistory.clear();
        evaluationsHistory.clear();
        alreadyUsed.clear();
        System.out.print("\u001B[33mThe history has been cleared.\u001B[0m");
    }

    /**
     * Show the secret code and terminate the program.
     */
    public static void quitGame() {
        System.out.print("The solution was: " + code);
        System.exit(0);
    }
}