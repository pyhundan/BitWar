import java.util.LinkedList;

/**
 * 保存历史记录类
 */
public class BattleHistory {

    /**
     * 用查表代替计算，判断得分
     */
    public static int ss1[][] = new int[][]{{1,5},{0,3}};
    public static int ss2[][] = new int[][]{{1,0},{5,3}};
    public static int no = 0;


    int number;
    Strategy s1,s2;
    /**
     * 存储顺序：s1决定,s2决定,s1得分,s2得分
     */
    LinkedList<Integer[]> result;
    int s1sum=0,s2sum=0;
    int s1_cop_time=0,s2_cop_time=0;

    public BattleHistory(Strategy stategy1,Strategy stategy2){
        s1 = stategy1;
        s2 = stategy2;
        result = new LinkedList<>();
        number = no++;
    }

    /**
     * 保存直接传入的数据
     * @param s1
     * @param s1result
     * @param s2
     * @param s2result
     */
    public void addresult(int s1,int s1result,int s2,int s2result){
        Integer [] temp = new Integer[4];
        temp[0] = s1;
        temp[1] = s2;
        temp[2] = s1result;
        temp[3] = s2result;
        result.addLast(temp);
    }

    /**
     * 控制台输出结果（测试用）
     */
    public void showresult(){
        System.out.println("s1决定,s2决定,s1得分,s2得分");

        for(int i=0;i<result.size();i++){
            Integer [] a = result.get(i);
            s1sum += a[2];
            s2sum += a[3];
            s1_cop_time += a[0];
            s2_cop_time += a[1];
            System.out.println(a[0] + " " + a[1] + " " + a[2] + " " + a[3]);
        }
        System.out.println("策略  合作次数    背叛次数    总得分");
        System.out.println(s1.name+"    "+s1_cop_time+"     " +(result.size()-s1_cop_time)+"      "+s1sum);
        System.out.println(s2.name+"    "+s2_cop_time+"     " +(result.size()-s2_cop_time)+"      "+s2sum);

    }

}


