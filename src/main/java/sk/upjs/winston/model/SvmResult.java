package sk.upjs.winston.model;

/**
 * Created by stefan on 2/16/15.
 */
public class SvmResult extends AnalysisResult{
    public static final String KERNEL_LINEAR_KERNEL = "LinearKernel";
    public static final String KERNEL_RBF_KERNEL = "RBFKernel";

    private String kernel;
    private double complexityConstant;
    private double gamma;

    public SvmResult(long analysis_id, double rmse, double meanAbsoluteError, int correctlyClassified, int incorrectlyClassified, String summary, String kernel, double complexityConstant, double gamma) {
        super(analysis_id, rmse, meanAbsoluteError, correctlyClassified, incorrectlyClassified, summary);
        this.kernel = kernel;
        this.complexityConstant = complexityConstant;
        this.gamma = gamma;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getComplexityConstant() {
        return complexityConstant;
    }

    public void setComplexityConstant(double complexityConstant) {
        this.complexityConstant = complexityConstant;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }
}
