package io.metaloom.jdlib.examples.clustering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import io.metaloom.jdlib.Jdlib;
import io.metaloom.jdlib.util.FaceDescriptor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FacesClustering extends Application {

	private BufferedImage loadImage(String imagepath) {
		try {
			return ImageIO.read(new File(imagepath));
		} catch (IOException e) {
			System.err.println("Error During Loading File: " + imagepath);
			return null;
		}
	}

	private Image convertToJFXImage(BufferedImage image) {
		return SwingFXUtils.toFXImage(image, null);
	}

	private LinkedHashMap<Image, FaceDescriptor> splitImage(Image img, List<List<FaceDescriptor>> faces) {
		LinkedHashMap<Image, FaceDescriptor> imageDic = new LinkedHashMap<>();
		PixelReader reader = img.getPixelReader();

		for (List<FaceDescriptor> faceDescriptors : faces) {
			for (FaceDescriptor faceDescriptor : faceDescriptors) {
				WritableImage cropedimage = new WritableImage(reader, faceDescriptor.getFaceBox().x, faceDescriptor.getFaceBox().y,
					faceDescriptor.getFaceBox().width, faceDescriptor.getFaceBox().height);
				imageDic.put(cropedimage, faceDescriptor);
			}
		}
		return imageDic;
	}

	private List<ImageView> getImageViews(Set<Image> imgs) {
		List<ImageView> imageviews = new LinkedList<>();
		for (Image img : imgs) {
			ImageView imageView = new ImageView();
			imageView.setImage(img);
			imageView.setX(10);
			imageView.setY(10);
			imageView.setFitWidth(80);
			imageView.setFitHeight(80);
			imageView.setPreserveRatio(true);
			imageviews.add(imageView);
		}
		return imageviews;
	}

	private double[] convertFloatsToDoubles(float[] input) {
		double[] output = new double[input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = input[i];
		}
		return output;
	}

	private List<List<FaceDescriptor>> clusterFaces(List<FaceDescriptor> facedescriptors) {
		DBSCANClusterer<DoublePoint> clusterAlgo = new DBSCANClusterer<>(0.6, 2);
		List<DoublePoint> ar = new ArrayList<>();
		facedescriptors.stream().forEach(facedescriptor -> {
			ar.add(new DoublePoint(convertFloatsToDoubles(facedescriptor.getFaceEmbedding())));
		});

		List<Cluster<DoublePoint>> clusters = clusterAlgo.cluster(ar);

		List<List<FaceDescriptor>> clusterlist = new ArrayList<>();
		for (int i = 0; i < clusters.size(); i++) {
			Cluster<DoublePoint> cluster = clusters.get(i);
			String label = "P: " + i;
			List<FaceDescriptor> clusterfaces = new ArrayList<>();
			cluster.getPoints().stream().forEach(point -> {
				facedescriptors.stream().filter(
					facedescriptor -> Arrays.equals(convertFloatsToDoubles(facedescriptor.getFaceEmbedding()), point.getPoint()))
					.forEach(facedescriptor -> {
						facedescriptor.setLabel(label);
						clusterfaces.add(facedescriptor);
					});
			});
			clusterlist.add(clusterfaces);
		}
		return clusterlist;
	}

	@Override
	public void start(Stage primaryStage) {
		BufferedImage img = loadImage("test_img/bald_guys_1.jpg");
		Jdlib jdlib = new Jdlib("shape_predictor_68_face_landmarks.dat", "dlib_face_recognition_resnet_model_v1.dat", "mmod_human_face_detector.dat");
		List<FaceDescriptor> faces = jdlib.getFaceEmbeddings(img);

		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(5);

		Button animatebtn = new Button();
		animatebtn.setText("Cluster Faces");
		vbox.getChildren().add(animatebtn);

		HBox image_container = new HBox();
		Image image = convertToJFXImage(img);
		ImageView imgview = new ImageView(image);
		imgview.setFitHeight(600);
		imgview.setFitWidth(600);
		image_container.getChildren().add(imgview);
		image_container.setPadding(new Insets(0, 0, 0, 10));
		vbox.getChildren().add(image_container);

		Scene scene = new Scene(vbox, 1200, 700);

		primaryStage.setTitle("Jdlib | Face Clustering");
		primaryStage.setScene(scene);
		primaryStage.show();

		animatebtn.setOnAction((ActionEvent event) -> {
			List<List<FaceDescriptor>> clusterlist = clusterFaces(faces);
			int numcluster = clusterlist.size();

			Map<Image, FaceDescriptor> imageDic = splitImage(image, clusterlist);
			List<ImageView> imageviews = getImageViews(imageDic.keySet());

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 30));

			int z = 0;
			for (int i = 0; i < numcluster; i++) {
				for (int j = 0; j < clusterlist.get(i).size(); j++) {
					grid.add(imageviews.get(z), j + 1, i + 1);
					z++;
				}
			}

			HBox b = (HBox) vbox.getChildren().get(1);
			if (b.getChildren().size() > 1) {
				b.getChildren().remove(1);
			}
			b.getChildren().add(1, grid);
			animateClustering(imageviews);
		});
	}

	private void animateClustering(List<ImageView> imgsv) {
		Timeline beat = new Timeline();
		int i = 0;
		for (ImageView imageView : imgsv) {
			DoubleProperty scale = new SimpleDoubleProperty(0);
			imageView.scaleXProperty().bind(scale);
			imageView.scaleYProperty().bind(scale);
			beat.getKeyFrames().add(new KeyFrame(Duration.seconds(0.4 * i), event -> scale.setValue(1)));
			i++;
		}
		beat.setAutoReverse(true);
		beat.setCycleCount(1);
		beat.play();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
