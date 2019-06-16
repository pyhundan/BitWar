package analyze;

import modle.Users;

import java.util.LinkedList;

public class History {

    //S1，S2的得分策略
    public static int s1[][]=new int [][]{{1,5},{0,3}};
    public static int s2[][]=new int [][]{{1,0},{5,3}};
    public static int no=0;

    int number;
    Users ss1,ss2;

    public LinkedList<Integer[]> result;
    int s1_sum=0,s2_sum=0;
    int s1_time=0,s2_time=0;

    public History(Users users1,Users users2)
    {
        ss1=users1;
        ss2=users2;
        result=new LinkedList<>();
        number=no++;
    }

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
            s1_sum += a[2];
            s2_sum += a[3];
            s1_time += a[0];
            s2_time += a[1];
            System.out.println(a[0] + " " + a[1] + " " + a[2] + " " + a[3]);
        }
        System.out.println("策略  合作次数    背叛次数    总得分");
        System.out.println(ss1.name+"    "+s1_time+"     " +(result.size()-s1_time)+"      "+s1_sum);
        System.out.println(ss2.name+"    "+s2_time+"     " +(result.size()-s2_time)+"      "+s2_sum);

    }

}
