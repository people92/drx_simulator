
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Simulation3 {

	// DRX 주기 = 10, 20, 40, 80, 320, 640, 1280, 2560, 5120, 10240ms
	public static String notApplySrOffset(int drx) {

		Random random = new Random();

		// 전체 24시간(86400000ms)
		int total = 86400000;

		// 활성구간 갯수 변수
		int count = 0;
		// 비활성구간 갯수 변수
		int not_count = 0;

		// Uplink 4ms(SR->grant->trasmission->ack 4ms 단위)
		int uplink_period = 4;

		// UL 발생주기 = 10분(600000ms)
		int uplink_random_period = 600000;

		// ul 데이터 발생 주기 10분당 1회 랜덤(SR->grant->transmission->ack 한세트)
		int uplink_random = random.nextInt(uplink_random_period);
		int downlink_random = random.nextInt(5) + 1;

		// 업링크 배열 선언
		int[] uplink_array = new int[total];

		// 다운운링크 배열 선언
		int[] downlink_array = new int[total];

		// 활성구간 배열 선언
		int[] active_array = new int[total];

		int downlink_count = 0;
		
		// 다운링크 drx주기마다 Onduration 1로 표현
		for (int i = 0; i < total; i++) {
			if ( (i + 1) % drx == 1) {
				downlink_array[i] = 1;
				downlink_count ++;
				
				if(downlink_count == downlink_random){
					downlink_array[i+1] = 1;
					downlink_count = 0;
					downlink_random = random.nextInt(5) + 1;
				}
			}
		}

		// SR 재발행을 위한 Flag
		boolean endFlag = false;

		// UL 발생 주기 10분마다 체크를 위한 변수
		int time_count = 1;

		// Uplink 세팅
		for (int i = 0; i < uplink_array.length; i++) {

			// Scheduling Request
			if (i == uplink_random) {
				uplink_array[i] = 1;
			}
			// UL Grant
			if (i == (uplink_random + uplink_period)) {
				uplink_array[i] = 1;
			}

			// UL Data transmission
			if (i == (uplink_random + uplink_period * 2)) {
				uplink_array[i] = 1;
			}
			// UL ack
			if (i == (uplink_random + uplink_period * 3)) {
				uplink_array[i] = 1;
				endFlag = true;
			}

			if (endFlag == true && (uplink_random + uplink_period * 3) < total ) {
				uplink_random = random.nextInt(uplink_random_period)+ (uplink_random_period * time_count);
				time_count++;
				endFlag = false;
			}

		}

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

		System.out.println(
				"******************************* [SR오프셋 미적용] DRX주기: " + drx + "*******************************");
//		System.out.println("UpLink : " + Arrays.toString(uplink_array));
//		System.out.println("DownLink : " + Arrays.toString(downlink_array));
//		System.out.println("active : " + Arrays.toString(active_array));
		System.out.println("총활성개수: " + count);
		System.out.println("총비활성개수: " + not_count);
		System.out.println("에너지: " + (count*0.5 + not_count*0.01));
		return count + ";" + not_count;

	}

	// DRX 주기 = 10, 20, 40, 80, 320, 640, 1280, 2560, 5120, 10240ms
	public static String ApplySrOffset(int drx) {

		Random random = new Random();

		// 전체 24시간(86400000ms)
		int total = 86400000;

		// 활성구간 갯수 변수
		int count = 0;
		// 비활성구간 갯수 변수
		int not_count = 0;

		// Uplink 4ms(SR->grant->trasmission->ack 4ms 단위)
		int uplink_period = 4;

		// UL 발생주기 = 10분(600000ms)
		int uplink_random_period = 600000;

		// ul 데이터 발생 주기 10분당 1회 랜덤(SR->grant->transmission->ack 한세트)
		int uplink_random = random.nextInt(uplink_random_period);
		int downlink_random = random.nextInt(5) + 1;

		// 업링크 배열 선언
		int[] uplink_array = new int[total];

		// 다운운링크 배열 선언
		int[] downlink_array = new int[total];

		// 활성구간 배열 선언
		int[] active_array = new int[total];

		int downlink_count = 0;
		
		// 다운링크 drx주기마다 Onduration 1로 표현
		for (int i = 0; i < total; i++) {
			if ( (i + 1) % drx == 1) {
				downlink_array[i] = 1;
				downlink_count ++;
				
				if(downlink_count == downlink_random){
					downlink_array[i+1] = 1;
					downlink_count = 0;
					downlink_random = random.nextInt(5) + 1;
				}
			}
		}
		
		// SR 재발행을 위한 Flag
		boolean endFlag = false;

		// UL 발생 주기 10분마다 체크를 위한 변수
		int time_count = 1;
		
		// Uplink 세팅
		for (int i = 0; i < uplink_array.length; i++) {

			// SR이 발생했을때 DRX 활성주기에 맞춤
			// 확인을 위해 원래 발생했던 SR에는 2로 표시
//			if (i == uplink_random) {
//				if (downlink_array[i] != 1) {
//					for (int j = i+1; j <= uplink_random_period; j++) {
//						if (downlink_array[j] == 1) {
//							uplink_array[i] = 0;
//							uplink_random = j;
//							break;
//						}
//					}
//				}
//			}

			// Scheduling Request
			if (i == uplink_random) {
				if (downlink_array[i] != 1) {
					for (int j = i; j <- uplink_array.length; j++) {
						if (downlink_array[j] == 1) {
							uplink_array[i] = 2;
							uplink_random = j;
							break;
						}
					}
				}
				else {
					uplink_array[i] = 1;
				}
			}
			// UL Grant
			else if (i == (uplink_random + uplink_period)) {
				uplink_array[i] = 1;
			}

			// UL Data transmission
			else if (i == (uplink_random + uplink_period * 2)) {
				uplink_array[i] = 1;
			}
			// UL ack
			else if (i == (uplink_random + uplink_period * 3)) {
				uplink_array[i] = 1;
				endFlag = true;
			}
			else if (endFlag == true && (uplink_random + uplink_period * 3) < total ) {
				uplink_random = random.nextInt(uplink_random_period) + (uplink_random_period * time_count);
				time_count++;
				endFlag = false;
			}

		}
		

		// 활성구간 구하기 Uplink OR DownLink
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
		

		System.out.println("******************************* [SR오프셋 적용] DRX주기: " + drx + "*******************************");
//		System.out.println("UpLink : " + Arrays.toString(uplink_array));
//		System.out.println("DownLink : " + Arrays.toString(downlink_array));
//		System.out.println("active : " + Arrays.toString(active_array));
		System.out.println("총활성개수: " + count);
		System.out.println("총비활성개수: " + not_count);
		System.out.println("에너지: " + (count*0.5 + not_count*0.01));
		
		return count + ";" + not_count;

	}
	
	public static void main(String[] args) {
		notApplySrOffset(10);
		//ApplySrOffset(10);
	}


}
