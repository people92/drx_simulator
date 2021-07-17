
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/*
   2번째 시뮬레이터 :  다운링크 데이터를 업링크 데이터에 맞춤
* */
public class Simulation2 {

	// 종래 방식 활성구간 구하기
	public String notApplyDlOffset(int drx, int uplink_period) {

		Random random = new Random();

		// 30 싸이클동안
		int cycle = 30;

		// drx 주기
		int drx_period = drx;

		// Ul Data 1ms, 2ms 생성
		int ulData = random.nextInt(2) + 1;

		// 전체 배열 크기 : drx주기 X 30
		int total = cycle * drx_period;

		// 업링크 배열 선언
		int[] uplink_array = new int[total];

		// 다운운링크 배열 선언
		int[] downlink_array = new int[total];

		// 활성구간 배열 선언
		int[] active_array = new int[total];

		// SR 랜덤 발행을 위한 변수
		int uplink_random = random.nextInt(drx_period * 5);

		// SR 재발행을 위한 Flag
		boolean endFlag = false;

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

			// ULData 1ms일때
			if (ulData == 1) {
				// UL Data transmission
				if (i == (uplink_random + uplink_period * 2)) {
					uplink_array[i] = 1;
				}
				// UL ack
				if (i == (uplink_random + uplink_period * 3)) {
					uplink_array[i] = 1;
					endFlag = true;
				}
				// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
				if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
					uplink_random = random.nextInt(drx_period * 5) + i;
					if (uplink_random > (i + uplink_period * 5)) {
						uplink_random = i + drx_period * 5;
					}
					endFlag = false;

				}
			}
			// ULData 2ms일때
			else if (ulData == 2) {

				// UL Data transmission
				if (i < total - 1 && i == (uplink_random + uplink_period * 2)) {
					uplink_array[i] = 1;
					uplink_array[i + 1] = 1;
				}
				// UL ack
				if (i < total - 1 && i == (uplink_random + uplink_period * 3)) {
					uplink_array[i + 1] = 1;
					endFlag = true;
				}
				// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
				if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
					uplink_random = random.nextInt(drx_period * 5) + i + 1;

					if (uplink_random > (i + uplink_period * 5)) {
						uplink_random = i + drx_period * 5;
					}

					endFlag = false;

				}
			}

		}
		// DLData 랜덤발생 1ms ~ 4ms
		int dlData = random.nextInt(4) + 1;

		// Downlink 세팅
		for (int i = 0; i < downlink_array.length; i++) {
			if ((i + 1) % drx_period == 1) {
				downlink_array[i] = 1; // Onduration
				for (int j = 1; j <= dlData; j++) { // DlData 크기만큼
					downlink_array[++i] = 1;
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

		System.out.println("******************************* [DL오프셋 미적용] DRX주기: " + drx + " / UL DATA: " + ulData
				+ "ms *******************************");
		System.out.println("UpLink   : " + Arrays.toString(uplink_array));
		System.out.println("DownLink : " + Arrays.toString(downlink_array));
		System.out.println("active   : " + Arrays.toString(active_array));
		System.out.println("총활성개수: " + count);
		System.out.println("총비활성개수: " + not_count);
		System.out.println("에너지: " + (count*0.5 + not_count*0.01));
		
		return count + ";" + not_count;
	}

	// 다운링크 오프셋 적용한 경우
	public String applyDlOffset(int drx, int uplink_period) {
		Random random = new Random();
		// 30 싸이클동안
		int cycle = 30;

		// drx 주기
		int drx_period = drx;

		// Ul Data 1ms, 2ms 생성
		// int ulData = random.nextInt(2) + 1;
		int ulData = random.nextInt(5) + 1;
		// 전체 배열 크기 : drx주기 X 30
		int total = cycle * drx_period;

		// 업링크 배열 선언
		int[] uplink_array = new int[total];

		// 다운링크 배열 선언
		int[] downlink_array = new int[total];

		// 활성구간 배열 선언
		int[] active_array = new int[total];

		// SR 랜덤 발행을 위한 변수
		int uplink_random = random.nextInt(drx_period * 5);

		// SR 재발행을 위한 Flag
		boolean endFlag = false;

		boolean uplinkDurationCheckFlag = false;

		// Downlink 세팅
		for (int i = 0; i < downlink_array.length; i++) {
			if ((i + 1) % drx_period == 1) {
				downlink_array[i] = 1; // Onduration
			}
		}
		// Uplink 세팅
		for (int i = 0; i < uplink_array.length; i++) {

			if (downlink_array[i] == 1) {
				uplinkDurationCheckFlag = false;
			}
			if (uplinkDurationCheckFlag == false) {

				

				// Scheduling Request
				if (i == uplink_random) {
					uplink_array[i] = 1;

				}

				// UL Grant
				if (i == (uplink_random + uplink_period)) {
					uplink_array[i] = 1;
				}

				// ULData 1ms일때
				if (ulData == 1) {

					// UL Data transmission
					// ULData 값 3으로 체크(구별하기 편하게 하기위함)
					if (i == (uplink_random + uplink_period * 2)) {
						uplink_array[i] = 3;
					}
					// UL ack
					if (i == (uplink_random + uplink_period * 3)) {
						uplink_array[i] = 1;
						endFlag = true;
						uplinkDurationCheckFlag = true;
					}
					// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
					if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
						//ulData = random.nextInt(5) + 1;
						uplink_random = random.nextInt(drx_period * 5) + i;
						if (uplink_random > (i + uplink_period * 5)) {
							uplink_random = i + drx_period * 5;
						}
						endFlag = false;

					}
				}
				// ULData 2ms일때
				else if (ulData == 2) {

					// UL Data transmission
					// ULData 값 3으로 체크(구별하기 편하게 하기위함)
					if (i < total - 1 && i == (uplink_random + uplink_period * 2)) {
						uplink_array[i] = 3;
						uplink_array[i + 1] = 3;
					}
					// UL ack
					if (i < total - 1 && i == (uplink_random + uplink_period * 3)) {
						uplink_array[i + 1] = 1;
						endFlag = true;
						uplinkDurationCheckFlag = true;
					}
					// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
					if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
						//ulData = random.nextInt(5) + 1;
						uplink_random = random.nextInt(drx_period * 5) + i + 1;

						if (uplink_random > (i + uplink_period * 5)) {
							uplink_random = i + drx_period * 5;
						}

						endFlag = false;

					}
				} else if (ulData == 3) {
					
					// UL Data transmission
					// ULData 값 3으로 체크(구별하기 편하게 하기위함)
					if (i < total - 2 && i == (uplink_random + uplink_period * 2)) {
						uplink_array[i] = 3;
						uplink_array[i + 1] = 3;
						uplink_array[i + 2] = 3;
					}
					// UL ack
					if (i < total - 2 && i == (uplink_random + uplink_period * 3)) {
						uplink_array[i + 2] = 1;
						endFlag = true;
						uplinkDurationCheckFlag = true;
					}
					// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
					if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
						//ulData = random.nextInt(5) + 1;
						uplink_random = random.nextInt(drx_period * 5) + i + 1;

						if (uplink_random > (i + uplink_period * 5)) {
							uplink_random = i + drx_period * 5;
						}

						endFlag = false;

					}
					
				} else if (ulData == 4) {
					// UL Data transmission
					// ULData 값 3으로 체크(구별하기 편하게 하기위함)
					if (i < total - 3 && i == (uplink_random + uplink_period * 2)) {
						uplink_array[i] = 3;
						uplink_array[i + 1] = 3;
						uplink_array[i + 2] = 3;
						uplink_array[i + 3] = 3;
					}
					// UL ack
					if (i < total - 3 && i == (uplink_random + uplink_period * 3)) {
						uplink_array[i + 3] = 1;
						endFlag = true;
						uplinkDurationCheckFlag = true;
					}
					// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
					if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
						//ulData = random.nextInt(5) + 1;
						uplink_random = random.nextInt(drx_period * 5) + i + 1;

						if (uplink_random > (i + uplink_period * 5)) {
							uplink_random = i + drx_period * 5;
						}

						endFlag = false;

					}

				} else if (ulData == 5) {
					// UL Data transmission
					// ULData 값 3으로 체크(구별하기 편하게 하기위함)
					if (i < total - 4 && i == (uplink_random + uplink_period * 2)) {
						uplink_array[i] = 3;
						uplink_array[i + 1] = 3;
						uplink_array[i + 2] = 3;
						uplink_array[i + 3] = 3;
						uplink_array[i + 4] = 3;
					}
					// UL ack
					if (i < total - 4 && i == (uplink_random + uplink_period * 3)) {
						uplink_array[i + 4] = 1;
						endFlag = true;
						uplinkDurationCheckFlag = true;
					}
					// 만약 SR->grant->tranmission->ack 이단계가 끝났을 시 SR 다시 랜덤으로 발행
					if (endFlag == true && ((uplink_random + uplink_period * 3) < uplink_array.length)) {
						//ulData = random.nextInt(5) + 1;
						uplink_random = random.nextInt(drx_period * 5) + i + 1;

						if (uplink_random > (i + uplink_period * 5)) {
							uplink_random = i + drx_period * 5;
						}

						endFlag = false;

					}

				}
			}

		}

		// // DLData 랜덤발생 1ms ~ 4ms
		// int dlData = random.nextInt(4) + 1;
		//
		// // Downlink 세팅
		// for (int i = 0; i < downlink_array.length; i++) {
		// if ((i + 1) % drx_period == 1) {
		// downlink_array[i] = 1; // Onduration
		//
		// // for (int j = 1; j <= dlData; j++) {
		// // downlink_array[++i] = 2; //DlData 크기만큼
		// // }
		// //
		// // //DonwLink Data를 Uplink에 맞춤
		// // for (int di = i; di < i + drx_period; di++) {
		// // try {
		// // //ULData 값일때
		// // if (uplink_array[di] == 3) {
		// // for (int dlDataLen = 0; dlDataLen < dlData; dlDataLen++) {
		// //
		// // if (di + dlDataLen < i + drx_period) {
		// // downlink_array[di + dlDataLen] = 1;
		// // }
		// //
		// // }
		// //
		// // }
		// // } catch (ArrayIndexOutOfBoundsException e) {
		// // break;
		// // }
		// // }
		//
		// }
		// }

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

		System.out.println("******************************* [DL오프셋 적용] DRX주기: " + drx + " / UL DATA: " + ulData
				+ "ms *******************************");
		System.out.println("UpLink   : " + Arrays.toString(uplink_array));
		System.out.println("DownLink : " + Arrays.toString(downlink_array));
		System.out.println("active   : " + Arrays.toString(active_array));
		System.out.println("총활성개수: " + count);
		System.out.println("총비활성개수: " + not_count);
		System.out.println("에너지: " + (count*0.5 + not_count*0.01));
		
		return count + ";" + not_count;
	}
}