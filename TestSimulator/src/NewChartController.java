
import java.util.Random;

import javax.xml.soap.Node;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class NewChartController extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Simulator");

		GridPane root = new GridPane();
		root.setGridLinesVisible(true);
		
		Label header1 = new Label("  ※시뮬레이션(SR오프셋)");
		header1.setId("header");

		root.add(header1, 0, 0);
		root.add(getSROffsetSimulationChart("활성구간"), 0, 1);
		root.add(getSROffsetSimulationChart("에너지"), 1, 1);

		Label header2 = new Label("  ※시뮬레이션(Delay)");

		header2.setId("header");

		root.add(header2, 2, 0);
		root.add(getDelayValueChart(), 2, 1);

		Label header3 = new Label("  ※시뮬레이션(DL오프셋)");

		header3.setId("header");

		root.add(header3, 0, 4);
		root.add(getDLOffsetSimulationChart("활성구간"), 0, 5);
		root.add(getDLOffsetSimulationChart("에너지"), 1, 5);
		
		Label header4 = new Label("  ※시뮬레이션(Delay)");

		header4.setId("header");

		root.add(header4, 2, 4);
		root.add(getDelayValueChart2(), 2, 5);

		// Scene 생성 Size: 1220x880
		Scene scene = new Scene(root, 1850, 900);
		// css 적용
		scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

		stage.setScene(scene);
		stage.show();

		System.out.println("END");
	}

	public LineChart<Number, Number> getDelayValueChart() throws Exception {
		Random random = new Random();
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("Delay");
		lineChart.setCreateSymbols(false);
		lineChart.setMinWidth(600);
		lineChart.setMaxWidth(600);
		lineChart.setMaxHeight(400);
		lineChart.setMinHeight(400);
		// x축 Label
		xAxis.setLabel("Traffic");
		// y축 Label
		yAxis.setLabel("Delay");

		// drx주기 배열
		int[] drxs = { 10240, 20480, 40960 };
		for (int drx : drxs) {
			XYChart.Series series1 = new XYChart.Series();
			for (int i = 1; i <= 100; i++) {
				int random_value = random.nextInt(drx);
				series1.setName("DRX 주기 : " + drx);
				series1.getData().add(new XYChart.Data<Number, Number>(i, random_value));
			}
			lineChart.getData().add(series1);
		}

		return lineChart;

	};
	
	public LineChart<Number, Number> getDelayValueChart2() throws Exception {
		Random random = new Random();
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("Delay");
		lineChart.setCreateSymbols(false);
		lineChart.setMinWidth(600);
		lineChart.setMaxWidth(600);
		lineChart.setMaxHeight(400);
		lineChart.setMinHeight(400);
		// x축 Label
		xAxis.setLabel("Traffic");
		// y축 Label
		yAxis.setLabel("Delay");

		// drx주기 배열
		int[] drxs = { 10, 20 , 40, 80 };
		for (int drx : drxs) {
			XYChart.Series series1 = new XYChart.Series();
			for (int i = 1; i <= 100; i++) {
				int random_value = random.nextInt(drx);
				series1.setName("DRX 주기 : " + drx);
				series1.getData().add(new XYChart.Data<Number, Number>(i, random_value));
			}
			lineChart.getData().add(series1);
		}

		return lineChart;

	};

	/**
	 * UL Data사이즈를 파라미터로 받아 SR오프셋 시뮬레이션 Chart를 생성하는 메소드 DRX주기 4, 8, 12, 16, 20,
	 * 24
	 *
	 * @param ulData:
	 *            UL Data사이즈
	 * @return
	 * @throws Exception
	 */
	public BarChart<String, Number> getSROffsetSimulationChart(String name) throws Exception {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
	
		
		// Chart title 생성
		if (name.equals("활성구간")) {
			barChart.setTitle("총 활성구간");
			yAxis.setLabel("총활성구간");
		} else if (name.equals("에너지")) {
			barChart.setTitle("총 전력(에너지)");
			yAxis.setLabel("총 전력(에너지)");
		}

		// Chart Size 적용
		barChart.setMinWidth(600);
		barChart.setMaxWidth(600);
		barChart.setMaxHeight(400);
		barChart.setMinHeight(400);

		// x축 Label
		xAxis.setLabel("DRX");
		// y축 Label

		// drx주기 배열
		int[] drxs = {2560, 5120, 10240 , 20480, 40960};

		Simulation3 simulation3 = new Simulation3();

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("미적용");
	
		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 SR오프셋 미적용 시뮬레이션을 한다.
			String result = simulation3.notApplySrOffset(drx);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);

			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series1.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				// x축: DRX주기, y축: 총활성구간
				series1.getData().add(new XYChart.Data(drx + "", power));
			}

		}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("적용");

		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 SR오프셋 적용 시뮬레이션을 한다.
			String result = simulation3.ApplySrOffset(drx);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);

			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series2.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				// x축: DRX주기, y축: 총활성구간
				series2.getData().add(new XYChart.Data(drx + "", power));
			}
		}
		barChart.getData().addAll(series1, series2);

		return barChart;
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
	public BarChart<String, Number> getDLOffsetSimulationChart(String name) throws Exception {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		// Chart title 생성
		if (name.equals("활성구간")) {
			barChart.setTitle("총 활성구간");
			yAxis.setLabel("총활성구간");
		} else if (name.equals("에너지")) {
			barChart.setTitle("총 전력(에너지)");
			yAxis.setLabel("총 전력(에너지)");
		}

		// Chart Size 적용
		barChart.setMinWidth(600);
		barChart.setMaxWidth(600);
		barChart.setMaxHeight(400);
		barChart.setMinHeight(400);

		// x축 Label
		xAxis.setLabel("DRX");
		// y축 Label

		// drx주기 배열
		int[] drxs = { 81920, 163840, 327680, 655360, 1310720 };
		//int[] drxs = { 10, 20, 40, 80, 320, 640, 1280, 2560, 5120, 10240 };
		
		Simulation4 simulation4 = new Simulation4();

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("미적용");

		for (int drx : drxs) {

			String result = simulation4.notApplyDlOffset(drx);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);

			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series1.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				// x축: DRX주기, y축: 총활성구간
				series1.getData().add(new XYChart.Data(drx + "", power));
			}

		}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("적용");

		for (int drx : drxs) {
			// DRX주기 4, 8, 12, 16, 20, 24 만큼 반복하여 SR오프셋 적용 시뮬레이션을 한다.
			String result = simulation4.applyDlOffset(drx);

			int active_result = Integer.parseInt(result.split(";")[0]);
			int not_active_result = Integer.parseInt(result.split(";")[1]);

			if (name.equals("활성구간")) {
				// x축: DRX주기, y축: 총활성구간
				series2.getData().add(new XYChart.Data(drx + "", active_result));
			} else if (name.equals("에너지")) {
				double power = (active_result * 0.5) + (not_active_result * 0.01);
				// x축: DRX주기, y축: 총활성구간
				series2.getData().add(new XYChart.Data(drx + "", power));
			}
		}
		barChart.getData().addAll(series1, series2);

		return barChart;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
