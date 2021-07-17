
import javafx.scene.chart.XYChart;

import java.util.*;

/*
   1번째 시뮬레이터 :  SR을 DRX 활성주기에 맞춤
* */
public class Simulation1 {

    //종래 방식 활성구간 구하기
    public String notApplySrOffset(int drx, int ulData) {

        Random random = new Random();

        //30 싸이클동안
        int cycle = 30;

        //Uplink 4ms
        int uplink_period = 4;

        //drx 주기
        int drx_period = drx;

        //전체 배열 크기 : drx주기 X 30
        int total = cycle * drx_period;

        //업링크 배열 선언
        int[] uplink_array = new int[total];

        //다운운링크 배열 선언
        int[] downlink_array = new int[total];

        //활성구간 배열 선언
        int[] active_array = new int[total];

        //다운링크 drx주기마다 Onduration 1로 표현
        for (int i = 0; i < total; i++) {
            if ((i + 1) % drx_period == 1) {
                downlink_array[i] = 1;
            }
        }

        //SR 재발행을 위한 Flag
        boolean endFlag = false;

        //SR 랜덤 발행을 위한 변수
        int uplink_random = random.nextInt(drx_period * 5);

        //Uplink 세팅
        for (int i = 0; i < uplink_array.length; i++) {

            //Scheduling Request
            if (i == uplink_random) {
                uplink_array[i] = 1;
            }
            //UL Grant
            if (i == (uplink_random + uplink_period)) {
                uplink_array[i] = 1;
            }

            //ULData 1ms일때
            if (ulData == 1) {

                //UL Data transmission
                if (i == (uplink_random + uplink_period * 2)) {
                    uplink_array[i] = 1;
                }
                //UL ack
                if (i == (uplink_random + uplink_period * 3)) {
                    uplink_array[i] = 1;
                    endFlag = true;
                }

                //만약 SR->grant->tranmission->ack  이단계가 끝났을 시 SR 다시 랜덤으로 발행
                if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
                    uplink_random = random.nextInt(drx_period * 5) + i;
                    if (uplink_random > (i + uplink_period * 5)) {
                        uplink_random = i + drx_period * 5;
                    }
                    endFlag = false;

                }
            }
            //ULData 2ms일때
            else if (ulData == 2) {

                //UL Data transmission
                if (i < total - 1 && i == (uplink_random + uplink_period * 2)) {
                    uplink_array[i] = 1;
                    uplink_array[i + 1] = 1;
                }
                //UL ack
                if (i < total - 1 && i == (uplink_random + uplink_period * 3)) {
                    uplink_array[i + 1] = 1;
                    endFlag = true;
                }
                //만약 SR->grant->tranmission->ack  이단계가 끝났을 시 SR 다시 랜덤으로 발행
                if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
                    uplink_random = random.nextInt(drx_period * 5) + i + 1;

                    if (uplink_random > (i + uplink_period * 5)) {
                        uplink_random = i + drx_period * 5;
                    }

                    endFlag = false;

                }
            }

        }


        //활성구간 갯수 변수
        int count = 0;
        int not_count = 0;

        for (int i = 0; i < uplink_array.length; i++) {
			if (uplink_array[i] == 1 || downlink_array[i] == 1) {
				active_array[i] = 1;
			} else {
				active_array[i] = 0;
			}
		}

		for (int i = 0; i < uplink_array.length; i++) {
			if (active_array[i] == 1) {
				count++;
			} else if(active_array[i] == 0){
				not_count++;
			}
		}
        System.out.println("******************************* [SR오프셋 미적용] DRX주기: " + drx + " / UL DATA: " + ulData + "ms *******************************");
        System.out.println("UpLink   : " + Arrays.toString(uplink_array));
        System.out.println("DownLink : " + Arrays.toString(downlink_array));
        System.out.println("active   : " + Arrays.toString(active_array));
        System.out.println("총활성개수: " + count);
        System.out.println("총비활성개수: " + not_count);
        System.out.println("에너지: " + (count*0.5 + not_count*0.01));
        
        return count + ";" + not_count;
    }

    //SR오프셋 적용한 경우
    public String applySrOffset(int drx, int ulData) {
        Random random = new Random();

        //30 싸이클동안
        int cycle = 30;

        //Uplink 4ms
        int uplink_period = 4;

        //drx 주기
        int drx_period = drx;

        //전체 배열 크기 : drx주기 X 30
        int total = cycle * drx_period;

        //업링크 배열 선언
        int[] uplink_array = new int[total];

        //다운링크 배열 선언
        int[] downlink_array = new int[total];

        //활성구간 배열 선언
        int[] active_array = new int[total];

        //다운링크 drx주기마다 Onduration 1로 표현
        for (int i = 0; i < downlink_array.length; i++) {
            if ((i + 1) % drx_period == 1) {
                downlink_array[i] = 1;
            }
        }

        //SR 재발행을 위한 Flag
        boolean endFlag = false;

        //SR 랜덤 발행을 위한 변수
        int uplink_random = random.nextInt(drx_period * 5);

        //Uplink 세팅
        for (int i = 0; i < uplink_array.length; i++) {

            //SR이 발생했을때  DRX 활성주기에 맞춤
            //확인을 위해 원래 발생했던 SR에는 2로 표시
            if (i == uplink_random) {
                if (downlink_array[i] != 1) {
                    for (int j = i; j < downlink_array.length; j++) {
                        if (downlink_array[j] == 1) {
                            uplink_array[i] = 2;
                            uplink_random = j;
                            break;
                        }
                    }
                }
            }

            //Scheduling Request
            if (i == uplink_random) {
                uplink_array[i] = 1;
            }
            //UL Grant
            if (i == (uplink_random + uplink_period)) {
                uplink_array[i] = 1;
            }

            //ULData 1ms일때
            if (ulData == 1) {

                //UL Data transmission
                if (i == (uplink_random + uplink_period * 2)) {
                    uplink_array[i] = 1;
                }
                //UL ack
                if (i == (uplink_random + uplink_period * 3)) {
                    uplink_array[i] = 1;
                    endFlag = true;
                }
                //만약 SR->grant->tranmission->ack  이단계가 끝났을 시 SR 다시 랜덤으로 발행
                if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
                    uplink_random = random.nextInt(drx_period * 5) + i;
                    if (uplink_random > (i + uplink_period * 5)) {
                        uplink_random = i + drx_period * 5;
                    }
                    endFlag = false;

                }
            }
            //ULData 2ms일때
            else if (ulData == 2) {

                //UL Data transmission
                if (i < total - 1 && i == (uplink_random + uplink_period * 2)) {
                    uplink_array[i] = 1;
                    uplink_array[i + 1] = 1;
                }
                //UL ack
                if (i < total - 1 && i == (uplink_random + uplink_period * 3)) {
                    uplink_array[i + 1] = 1;
                    endFlag = true;
                }
                //만약 SR->grant->tranmission->ack  이단계가 끝났을 시 SR 다시 랜덤으로 발행
                if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
                    uplink_random = random.nextInt(drx_period * 5) + i + 1;

                    if (uplink_random > (i + uplink_period * 5)) {
                        uplink_random = i + drx_period * 5;
                    }

                    endFlag = false;

                }
            }

        }

        //활성구간 갯수 변수
        int count = 0;
        int not_count = 0;

        for (int i = 0; i < uplink_array.length; i++) {
			if (uplink_array[i] == 1 || downlink_array[i] == 1) {
				active_array[i] = 1;
			} else {
				active_array[i] = 0;
			}
		}

		for (int i = 0; i < uplink_array.length; i++) {
			if (active_array[i] == 1) {
				count++;
			} else if(active_array[i] == 0){
				not_count++;
			}
		}
        System.out.println("******************************* [SR오프셋 적용] DRX주기: " + drx + " / UL DATA: " + ulData + "ms *******************************");
        System.out.println("UpLink   : " + Arrays.toString(uplink_array));
        System.out.println("DownLink : " + Arrays.toString(downlink_array));
        System.out.println("active   : " + Arrays.toString(active_array));
        System.out.println("총활성개수: " + count);
        System.out.println("총비활성개수: " + not_count);
        System.out.println("에너지: " + (count*0.5 + not_count*0.01));
        
        return count + ";" + not_count;
    }

}