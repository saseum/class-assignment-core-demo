package com.wkd.dev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static int[] solution(int[] sequence, int k) {
        int[] answer = null;
        List<int[]> subArray = new ArrayList<>();

        List<Integer> seqList = Arrays.stream(sequence)
                                        .boxed()
                                        .collect(Collectors.toCollection(ArrayList::new));

        // k 와 같은 배열요소 있을 경우
        if(seqList.contains(k)) {
            for(int i = 0; i < sequence.length; i++) {
                if(sequence[i] == k) return new int[] {i, i};
            }
        }
        else {

        }


        return answer;
    }
}
