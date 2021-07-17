import java.util.Arrays;
import java.util.Random;

public class Simulation4 {

	// 종래 방식 활성구간 구하기
	public String notApplyDlOffset(int drx) {
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
				"******************************* [DL오프셋 미적용] DRX주기: " + drx + "*******************************");
//		System.out.println("UpLink : " + Arrays.toString(uplink_array));
//		System.out.println("DownLink : " + Arrays.toString(downlink_array));
//		System.out.println("active : " + Arrays.toString(active_array));
		System.out.println("총활성개수: " + count);
		System.out.println("총비활성개수: " + not_count);
		System.out.println("에너지: " + (count*0.5 + not_count*0.01));
		
		return count + ";" + not_count;
	}

	// 다운링크 오프셋 적용한 경우
	public String applyDlOffset(int drx) {
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

		// UL GRANT-UL DATA 사이 10초(1000ms, 지연허용값)
		int delay_time = 1000;

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
			if (i == (uplink_random + uplink_period + delay_time)) {
				uplink_array[i] = 1;
				downlink_array[i] = 1;
				endFlag = true;
			}
			if (endFlag == true && (uplink_random + uplink_period * 3) < total) {
				uplink_random = random.nextInt(uplink_random_period)+ (uplink_random_period * time_count);
				time_count++;
				endFlag = false;
			}
		}
		
		// 활성구간 구하기 Uplink OR DownLink
		for (int i = 0; i < uplink_array.length; i++) {
			if (uplink_array[i] == 1 || downlink_array[i] == 1) {
				active_array[i] = 1;
			}
		}

		for (int i = 0; i < uplink_array.length; i++) {
			if (active_array[i] == 1) {
				count++;
			} else {
				not_count++;
			}
		}

		System.out.println(
				"******************************* [DL오프셋 적용] DRX주기: " + drx + "*******************************");
		// System.out.println("UpLink : " + Arrays.toString(uplink_array));
		// System.out.println("DownLink : " + Arrays.toString(downlink_array));
		// System.out.println("active : " + Arrays.toString(active_array));
		System.out.println("총활성개수: " + count);
		System.out.println("총비활성개수: " + not_count);
		System.out.println("에너지: " + (count*0.5 + not_count*0.01));
		return count + ";" + not_count;
	}

	

}
