package modle;

import analyze.StateAnalyze;
import analyze.WordAnalyze;

import java.io.BufferedReader;
import java.io.FileReader;

public class Users {

    public WordAnalyze wordAnalyze;
    public StateAnalyze stateAnalyze;
    public String name;
    public Users(String filepath)
    {
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(filepath));
            StringBuffer stringBuffer=new StringBuffer();
            String line=bufferedReader.readLine();
            while(line!=null)
            {
                stringBuffer.append(line+'\n');
                line=bufferedReader.readLine();
            }
            name=filepath;
            wordAnalyze=new WordAnalyze(stringBuffer.toString());
            wordAnalyze.analyse();
            stateAnalyze=new StateAnalyze(wordAnalyze.tokes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Users(){}

}
