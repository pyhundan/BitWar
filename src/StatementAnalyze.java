import java.util.HashMap;
import java.util.LinkedList;

/*
    文法规则：
    state_seq     ->  state { . state}
    state         ->  if_stmt | spin_stmt | assi_stmt | feedback_stmt
    if_stmt       ->  if( exp ) then '{' state_seq '}' {else '{' state_seq '}'} done
    spin_stmt     ->  operate for (exp) '{' state_seq '}'
    assi_stmt     ->  identifier : simple
    feedback_stmt ->  feedback (identifier | number)
    exp           ->  simple { compare simple}
    simple        ->  term { addop term}
    term          ->  factor { mulop term}
    factor        ->  (exp) | number | identifier | RANDOM | HISTORY_CALL
    HISTORY_CALL  ->  HISTORY[simple]

 */

/**
 * 执行文法分析的类
 */
public class StatementAnalyze {

    String fail;

    public LinkedList<Token> tokens;
    public Token cur;
    public int index;
    public HashMap<String,Integer> identifier;
    public int feedback;
    public boolean feedbackflag;
    public BattleHistory battleHistory;
    public int opponent;

    public void setBattleHistory(BattleHistory battleHistory,int op) {
        this.battleHistory = battleHistory;
        opponent = op;
    }

    /**
     *传入单词列表构建
     * @param list
     */
    public StatementAnalyze(LinkedList<Token> list){
        tokens = list;
        identifier = new HashMap<>();
        for(int i=0;i<tokens.size();i++){
            Token t = tokens.get(i);
            if(t.type.equals("identifier")){
                identifier.put(t.word,0);
            }
        }

    }

    /**
     * 以下都是语法分析的过程函数：遇到非终结符号进行函数调用，终结符号直接匹配
     */
    public void analysis(){
        index=0;
        feedbackflag = true;
        feedback = 0;
        try {
            cur = tokens.get(index++);
            state_seq();
        }
        catch (CodeException e){
            fail = e.message;
        }
    }

    private void state_seq()throws CodeException{
        String word = cur.word;
        if(word.equals("if") || word.equals("operate") || word.equals("feedback") || cur.type.equals("identifier")){
            state();
            while(cur.word.equals(".")){
                match(".");
                state();
            }
        }
        else Error("state_seq not corresponding! (index:"+(index-1)+",line:"+cur.line+")");
    }

    private void state() throws CodeException{
        String word = cur.word;
        if (word.equals("if")){
            if_stmt();
        }
        else if(word.equals("operate")){
            spin_stmt();
        }
        else if(word.equals("feedback")){
            feedback_stmt();
        }
        else if(cur.type.equals("identifier")){
            assi_stmt();
        }
        else Error("state not corresponding! (index:"+(index-1)+",line:"+cur.line+")");
    }

    private void if_stmt()throws CodeException{
        match("if");
        match("(");
        int tiaojian = exp();
        match(")");
        match("{");
        if(tiaojian == 1){
            state_seq();
        }
        else{
            Token t = cur;
            int i = index;
            while(!t.word.equals("}")){
                if(t.word.equals("{")){

                }
                t = tokens.get(i++);
            }
            cur = t;
            index = i;
        }
        match("}");
        if(cur.word.equals("else")){
            match("else");
            match("{");
            if(tiaojian == 1){
                /*Token t = cur;
                int i = index;
                while(!t.word.equals("}")){
                    t = tokens.get(i++);
                }
                cur = t;
                index = i;*/
                index = findleftbracket(index-1);
                cur = tokens.get(index++);
            }
            else{
                state_seq();
            }
            match("}");
        }
        match("done");
    }

    private void spin_stmt()throws CodeException{
        match("operate");
        match("for");
        match("(") ;
        int expindex = index -1;
        int temp = exp();
        match(")");
        match("{");
        int spinindex = index -1;
        //int spinlenth = 0;
        /*Token t = cur;
        while(!t.word.equals("}")){
            spinlenth++;
            t = tokens.get(spinindex + spinlenth);
        }*/
        int spinendindex = findleftbracket(index-1);
        while(temp == 1){
            state_seq();
            index = expindex;
            cur = tokens.get(index++);
            temp = exp();
            index = spinindex;
            cur = tokens.get(index++);
        }
        //index = spinindex + spinlenth;
        index = spinendindex;
        cur = tokens.get(index++);
        match("}");
    }

    private void assi_stmt()throws CodeException{
        String iden = cur.word;
        match("identifier");
        match(":");
        int temp = simpleexp();
        identifier.put(iden,temp);
    }

    private void feedback_stmt()throws CodeException{
        match("feedback");
        if(cur.type.equals("identifier")){
            if(feedbackflag){
                feedback = identifier.get(cur.word);
                feedbackflag = false;
            }
            match("identifier");
        }
        else if(cur.type.equals("number")){
            if(feedbackflag){
                feedback = Integer.parseInt(cur.word);
                feedbackflag = false;
            }
            match("number");
        }
        else Error("feedback not corresponding! (index:"+(index-1)+",line:"+cur.line+")");
    }

    private int exp()throws CodeException{
        int temp=simpleexp();
        while(cur.word.equals("=") || cur.word.equals("<") || cur.word.equals(">")){
            char compare = cur.word.charAt(0);
            match("character");
            int temp1 = simpleexp();
            switch (compare){
                case '=':
                    if(temp == temp1) temp = 1;
                    else temp = 0;
                    break;
                case '<':
                    if(temp < temp1) temp = 1;
                    else temp = 0;
                    break;
                case '>':
                    if(temp > temp1) temp = 1;
                    else temp = 0;
                    break;
            }
        }
        return temp;
    }

    private int simpleexp()throws CodeException{
        int temp = term();
        while(cur.word.equals("-") || cur.word.equals("+")){
            char c = cur.word.charAt(0);
            match("character");
            switch (c){
                case '-':temp -= term();break;
                case '+':temp += term();break;
            }
        }
        return temp;
    }

    private int term()throws CodeException{
        int temp = factor();
        while(cur.word.equals("*") || cur.word.equals("/")){
            char c = cur.word.charAt(0);
            match("character");
            switch (c){
                case '*':temp *= factor();break;
                case '/':temp /= factor();break;
            }
        }
        return temp;
    }

    private int factor()throws CodeException{
        int temp = 0;
        if(cur.word.equals("(")){
            match("(");
            temp = simpleexp();
            match(")");
        }
        else if(cur.type.equals("number")){
            temp = Integer.parseInt(cur.word);
            match("number");
        }
        else if(cur.type.equals("identifier")){
            temp = identifier.get(cur.word);
            match("identifier");
        }
        else if(cur.type.equals("system")){
            if(cur.word.equals("RANDOM")){
                temp = random();
            }
            else if (cur.word.equals("LATEST")){
                temp = battleHistory.result.size()-1;
                match("LATEST");
            }
            else {
                temp = history();
            }
        }
        else Error("not a factor! (index:"+(index-1)+",line:"+cur.line+")");
        return temp;
    }

    private int history() throws CodeException{
        match("HISTORY");
        match("[");
        int temp = simpleexp();
        match("]");
        //只为语法检测，不执行的时候
        if(battleHistory == null){
            return 0;
        }
        else return (battleHistory.result.get(temp))[opponent];
    }

    private int random() throws CodeException{
        match("RANDOM");
        match("(");
        int temp = (int)(Math.random()*simpleexp());
        match(")");
        return temp;
    }

    private void Error(String s) throws CodeException{
        System.err.print(s);
        throw new CodeException(s);
    }

    private void match(String a) throws CodeException{
        if(cur.word.equals(a) || cur.type.equals(a)){
            if(index < tokens.size()){
                cur = tokens.get(index++);
            }
        }
        else{
            Error("wrong word! expected: " + a + " actual: " + cur.word + " (index:" + (index-1)+",line:"+cur.line+")");
        }
    }

    /**
     * 递归寻找匹配‘{’同级的‘}’，用以分割代码段
     * @param curindex
     * @return
     */
    private int findleftbracket(int curindex){
        int temp = curindex;
        Token t = tokens.get(curindex);
        while(!t.word.equals("}")){
            if(t.word.equals("{")){
                temp = findleftbracket(temp+1);
            }
            t = tokens.get(++temp);
        }
        return temp;
    }

}
