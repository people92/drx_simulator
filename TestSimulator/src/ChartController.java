
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChartController extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Simulator");

		GridPane root = new GridPane();

		Label header1 = new Label("  ※시뮬레이션(SR오프셋)");
		header1.setId("header");
		// SR오프셋 제목 추가
		root.add(header1, 0, 0);
		// [위치: Left Top] Grid에 SR오프셋 시뮬레이션 차트를 생성한다. (UL Data: 1ms)
		root.add(getSimulation1Chart(1, "활성구간"), 0, 1);
		// [위치: Right Top] Grid에 SR오프셋 시뮬레이션 차트를 생성한다. (UL Data: 2ms)
		root.add(getSimulation1Chart(2, "활성구간"), 1, 1);
		root.add(getSimulation1Chart(1, "에너지"), 2, 1);

		Label header2 = new Label("  ※시뮬레이션(DL오프셋)");
		header2.setId("header");
		// DL오프셋 제목 추가
		root.add(header2, 0, 2);
		// [위치: Left Bottom] Grid에 DL오프셋 시뮬레이션 차트를 생성한다. (UL Data: 1ms)
		root.add(getSimulation2Chart(1, "활성구간"), 0, 3);
		// [위치: Right Bottom] Grid에 DL오프셋 시뮬레이션 차트를 생성한다. (UL Data: 2ms)
		root.add(getSimulation2Chart(2, "활성구간"), 1, 3);
		root.add(getSimulation2Chart(1, "에너지"), 2, 3);

		// Scene 생성 Size: 1220x880
		Scene scene = new Scene(root, 1850, 900);
		// css 적용
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * UL Data사이즈를 파라미터로 받아 SR오프셋 시뮬레이션 Chart를 생성하는 메소드 DRX주기 4, 8, 12, 16, 20,
	 * 24
	 *
	 * @param ulData:
	 *            UL Data사이즈
	 * @return
	 * @throws Exception
	 */
	public BarChart<String, Number> getSimulation1Chart(int ulData, String name) throws Exception {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
		
		// Chart Size 적용
		barChart.setMinWidth(600);
		barChart.setMaxWidth(600);
		barChart.setMaxHeight(400);
		barChart.setMinHeight(400);
		// x축 Label
		xAxis.setLabel("DRX");
		
		// Chart title 생성
		if (name.equals("활성구간")) {
			barChart.setTitle("총 활성구간 - " + "UL Data: " + ulData + "ms");
			yAxis.setLabel("총활성구간");
		} else if (name.equals("에너지")) {
			barChart.setTitle("총 전력(에너지) - " + "UL Data: " + ulData + "ms");
			yAxis.setLabel("총 전력(에너지)");
		}

		// drx주기 배열
		int[] drxs = new int[25];

		for (int i = 0; i < drxs.length; i++) {
			drxs[i] = (i + 1) * 4;
		}

		Simulation1 simulation1 = new Simulation1();

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("미적용");
		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 SR오프셋 미적용 시뮬레이션을 한다.
			String result = simulation1.notApplySrOffset(drx, ulData);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);
			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series1.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				series1.getData().add(new XYChart.Data(drx + "", power));
			}
		}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("적용");
		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 SR오프셋 적용 시뮬레이션을 한다.
			String result = simulation1.applySrOffset(drx, ulData);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);
			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series2.getData().add(new XYChart.Data(drx + "", active_result));
			}else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				series2.getData().add(new XYChart.Data(drx + "", power));
			}

		}
		barChart.getData().addAll(series1, series2);
		return barChart;
	}

	/**
	 * UL Data사이즈를 파라미터로 받아 DL오프셋 시뮬레이션 Chart를 생성하는 메소드 DRX주기 4, 8, 12, 16, 20,
	 * 24
	 *
	 * @param ulData:
	 *            UL Data사이즈
	 * @return
	 * @throws Exception
	 */
	public BarChart<String, Number> getSimulation2Chart(int ulData, String name) throws Exception {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
		
		// Chart Size 적용
		barChart.setMinWidth(600);
		barChart.setMaxWidth(600);
		barChart.setMaxHeight(400);
		barChart.setMinHeight(400);
		// x축 Label
		xAxis.setLabel("DRX");
		
		// Chart title 생성
		if (name.equals("활성구간")) {
			barChart.setTitle("총 활성구간 - " + "UL Data: " + ulData + "ms");
			yAxis.setLabel("총활성구간");
		} else if (name.equals("에너지")) {
			barChart.setTitle("총 전력(에너지) - " + "UL Data: " + ulData + "ms");
			yAxis.setLabel("총 전력(에너지)");
		}


		// drx주기 배열
		int[] drxs = new int[25];

		for (int i = 0; i < drxs.length; i++) {
			drxs[i] = (i + 1) * 4;
		}
		Simulation2 simulation2 = new Simulation2();

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("미적용");
		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 DL오프셋 미적용 시뮬레이션을 한다.
			String result = simulation2.notApplyDlOffset(drx, 4);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);

			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series1.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				series1.getData().add(new XYChart.Data(drx + "", power));
			}
		}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("적용");
		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 DL오프셋 적용 시뮬레이션을 한다.
			String result = simulation2.applyDlOffset(drx, 4);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);

			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series2.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				series2.getData().add(new XYChart.Data(drx + "", power));
			}
		}
		barChart.getData().addAll(series1, series2);
		return barChart;
	}

	public static void main(String[] args) {
		int tryCount = 0;
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
			// 에러발생 시 재실행
			if (tryCount++ < 5) {
				launch(args);
			}
		}

	}
}