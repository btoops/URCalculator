import java.util.Arrays;
import java.util.Scanner;

public class URCalculator {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Operation test = new Operation();
		String input = "";
		while(true) {
			input = sc.nextLine();
			if(input.equals("exit")) {
				break;
			}
			if(input.equals("show all")) {
				System.out.println(Arrays.asList(test.getVariables()));
			}
			else if(input.contains("clear")) {
				String[] temp = input.split("\\s");

				if(temp[1].equals("all")) {
					test.clearAll();

				}else {
					if(!test.clear(temp[1])) {
						System.out.println("That is not currently a variable");
					}
				}
			}else {
				if(input.contains("=")) {
					test.setVariables(input);
				}else {
					if(test.infixToPostfixEvaluation(input)) {
						System.out.println(test.getTotal());
					}
				}

			}
		}







		sc.close();

	}

}
