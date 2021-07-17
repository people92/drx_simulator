import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class Test {
    public static int[] solution(int[] numbers) {
        int[] answer = {};

        int size = numbers.length;

        List<Integer> list = new ArrayList<>();

        for(int i=0; i < size-1; i++){
            int temp = numbers[i] + numbers[i+1];
            if(!list.contains(temp)){
                list.add(temp);
            }
        }
        Collections.sort(list);
        
        int index = 0;
        for(Integer i : list){
        	answer[index++]=i;
        }
        return answer;
    }

    public static void main(String[] args) {
        int[] input = {2,1,3,4,1};
        int[] t = solution(input);

        for(int i=0; i < t.length; i++){
            System.out.println(t[i]);
        }
    }
}