package Aurvandil.Util;

public class ProgressBarUtil {
    private final int numberOfSteps;
    private int currentStep = 0;

    public ProgressBarUtil(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public void advance() {
        currentStep++;
        int numEquals = (int)(((double)currentStep / (double)numberOfSteps) * 100.0);
        int numSpace = 100 - numEquals;
        String bar = "[";
        for (int i = 0; i < numEquals; i++) {
            bar += "=";
        }
        for (int i = 0; i < numSpace; i++) {
            bar += " ";
        }
        //System.out.print("\r" + bar + "] " + numEquals + "%");
        System.out.printf("\r%s] ( %d / %d ) %d%s", bar, currentStep, numberOfSteps, numEquals, "%");

        if (currentStep == numberOfSteps) {
            System.out.println();
        }
    }
}