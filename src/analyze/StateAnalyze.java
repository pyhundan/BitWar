package analyze;

import com.sun.tools.javac.jvm.Code;
import error.CodeException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;


/*
    文法规则：

    stmt_seq -> statement { ; statement }
    statement -> if_stmt|repeat_stmt|assign_stmt |feedback_stme
    if_stmt -> if(exp)then '{' stmt_seq'}' {else '{'stmt_seq'}'} end
    repeat_stmt -> for(exp) '{'stmt_seq'}' end for
    assign_stmt -> identifier:=exp
    feedback_stmt -> feedback(identifier|number)
    exp -> simple_exp {comparison_op simple_exp}
    comparison_op -> <|=|>
    simple_exp -> term { addop term}
    addop -> +|-
    term -> factor {mulop factor}
    mulop -> *|/
    factor -> (exp)|number|identifier|RANDOM|HISTORY_CALL
    HISTORY_CALL -> HISTORY[simple_exp]
    RANDOM -> (random)

 */

public class StateAnalyze {

    String fail;

    public LinkedList<Token> tokens;
    public Token tok;
    public int index;
    public HashMap<String,Integer> identifier;
    public int feedback;
    public boolean feedbackflag;
    //public BattleHistory battleHistory
    //public int oppenent;

    //public void setBattleHistory(){};

    //构建标识符列表
    public StateAnalyze(LinkedList<Token> list)
    {
        this.tokens=list;
        identifier=new HashMap<>();
        for(int i=0;i<tokens.size();i++)
        {
            Token temp=tokens.get(i);
            if(temp.type.equals("identifier"))
            {
                identifier.put(temp.word,0);
            }
        }
    }
    //递归下降分析方法

    //随机数
    public int random() throws CodeException
    {
       match("RANDOM");
       match("(");
       int temp=(int)(Math.random());
       match(")");
       return temp;
    }
    //错误提示
    public void Error(String a) throws CodeException
    {
        System.err.print(a);
        throw new CodeException(a);
    }
    //匹配函数
    public void match(String a) throws CodeException
    {
        if(tok.word.equals(a)||tok.type.equals(a))
        {
            if (index<tokens.size())
            {
                tok=tokens.get(index++);
            }
        }
        else{
            Error("wrong word! expected "+a+"actual:"+tok.word+"index:"+(index-1)+";line"+tok.line);
        }
    }
    public void start_analyse()
    {
        index=0;
        feedbackflag=true;
        feedback=0;

        try {
            tok=tokens.get(index++);
            stmt_seq();
        }catch (CodeException e)
        {
            fail=e.message;
        }
    }

    public void stmt_seq() throws CodeException
    {
        String word=tok.word;

    }

    public void statement() throws CodeException
    {

    }

    public void if_stmt()
    {

    }

    public void repeat_stmt()
    {

    }

    public void assign_stmt()
    {

    }

    public void feedback_stmt()
    {

    }

    public void exp()
    {

    }

    public void comparison_op()
    {

    }

    public void simple_exp()
    {

    }

    public void addop()
    {

    }

    public void term()
    {

    }

    public void mulop()
    {

    }

    public void factor()
    {

    }

    public void HISTORY_CALL()
    {

    }



}
