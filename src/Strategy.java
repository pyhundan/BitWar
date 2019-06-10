import java.io.BufferedReader;
import java.io.FileReader;

/**
 * 策略实体类
 */
public class Strategy {

     WordAnalysis wordAnalysis;
     StatementAnalyze statementAnalyze;
     String name;

    /**
     * 通过文件名构建
     * @param filepath
     */
    public Strategy(String filepath){
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath));
            StringBuffer stringBuffer = new StringBuffer();
            String line = bufferedReader.readLine();
            while(line != null){
                stringBuffer.append(line + '\n');
                line = bufferedReader.readLine();
            }
            name = filepath;//.replaceAll("\\.txt","");
            wordAnalysis = new WordAnalysis(stringBuffer.toString());
            wordAnalysis.analysis();
            statementAnalyze = new StatementAnalyze(wordAnalysis.tokens);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Strategy(){

    }


}
