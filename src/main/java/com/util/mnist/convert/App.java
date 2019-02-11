package com.util.mnist.convert;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class App 
{
    public static void main( String[] args ) {
    		System.out.println("Launching ...");
	    	String TRAIN_IMAGES_FILE = "data/train-images.idx3-ubyte";
	    String TRAIN_LABELS_FILE = "data/train-labels.idx1-ubyte";
	    String TEST_IMAGES_FILE = "data/t10k-images.idx3-ubyte";
	    String TEST_LABELS_FILE = "data/t10k-labels.idx1-ubyte";
	    
	    StringBuilder [] trainims = getImages(TRAIN_IMAGES_FILE, 60000);
	    Double[] trainLabels = getLabels(TRAIN_LABELS_FILE);
	    
	    StringBuilder [] testims = getImages(TEST_IMAGES_FILE, 10000);
	    Double[] testLabels = getLabels(TEST_LABELS_FILE);
	    
	    for (int i = 0; i < trainLabels.length; i++) {
	    		trainims[i].append(trainLabels[i].toString());
	    }
	    
	    for (int j = 0; j < testLabels.length; j++) {
	    		testims[j].append(testLabels[j].toString());
	    }
	    writeData(testims, "test-images-labels-comb.txt");
	    writeData(trainims, "train-images-labels-comb.txt");
    }
  
    public static StringBuilder [] getImages(String fileName, int size) {
    		StringBuilder[] allData = new StringBuilder[size];
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000803".equals(bytesToHex(bytes))) {                        
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Integer.parseInt(bytesToHex(bytes), 16);       
                bin.read(bytes, 0, 4);
                int xPixel = Integer.parseInt(bytesToHex(bytes), 16);          
                bin.read(bytes, 0, 4);
                int yPixel = Integer.parseInt(bytesToHex(bytes), 16);          
                for (int i = 0; i < number; i++) {
                		StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < xPixel * yPixel; j++) {
                        Double txt = (double) bin.read();
                        builder.append(txt.toString() + "\t");
                    }
                    allData[i] = builder;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allData;
    }
    
    public static Double[] getLabels(String filename) {
        Double[] y = null;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000801".equals(bytesToHex(bytes))) {
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Integer.parseInt(bytesToHex(bytes), 16);
                y = new Double[number];
                for (int i = 0; i < number; i++) {
                    y[i] = (double) bin.read();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return y;
    }
    
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    
    public static double[][] appendLabels(double[][] images, double[] labels) {
    		double[][] appendedLabels = new double[images.length][images[0].length + 1];
    		for (int i = 0; i<images.length; i++) {
    			for (int j = 0; j<images[i].length; j++) {
    				appendedLabels[i][j] = images[i][j];
    			}
    			appendedLabels[i][images[0].length] = labels[i];
    		}
    		return appendedLabels;
    }
    
    public static void writeData(StringBuilder[] data, String filename) {
    		StringBuilder builder = new StringBuilder();
    		for (StringBuilder d : data) {
    			builder.append(d.toString() + "\n");
    		}
        
    		File datafile = new File(filename);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(datafile))) {
            System.out.println("File was written to: "  + datafile.getCanonicalPath());
            bw.write(builder.toString());
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
