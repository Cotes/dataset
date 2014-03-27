package com.ast.dataset.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ast.dataset.actions.ByteRange;
import com.ast.dataset.models.SimpleFile;
import com.ast.dataset.util.Config;

public abstract class DatasetGenerator {
	
	private Logger logger = Logger.getLogger( DatasetGenerator.class.getName() );
	public enum ModificationPart {B, E, M, BE, BM, ME, BEM};
	public enum Operation {ADD, UPDATE, REMOVE};
	
	private Random random;
	private int modificationSize;
	
	public DatasetGenerator() {
		this.random = new Random();
		this.modificationSize = Config.getModificationSize();
	}
	
	public abstract void generateDataset(String datasetFile, List<SimpleFile> files);

	protected ArrayList<ByteRange> generateUpdate(Object fileToModify, int fileSize) throws IOException {
        
        ModificationPart modificationPart = this.getModificationPart();
        
        int bs1, bs2, bs3 = 0;
        
        ArrayList<ByteRange> ranges = null;
        
        switch (modificationPart){
        case B:
        case E:
        case M:
            bs1 = this.getRealisticByteModification(fileSize, modificationPart);
            ranges = this.modifyFile(fileToModify, fileSize, bs1);
            break;
        case BE:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
            ranges = this.modifyFile(fileToModify, fileSize, bs1, bs2);
            break;
        case BM:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.M);
            ranges = this.modifyFile(fileToModify, fileSize, bs1, bs2);
            break;
        case ME:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.M);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
            ranges = this.modifyFile(fileToModify, fileSize, bs1, bs2);
            break;
        case BEM:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
            bs3 = this.getRealisticByteModification(fileSize, ModificationPart.M);
            ranges = this.modifyFile(fileToModify, fileSize, bs1, bs2, bs3);
            break;
        }
        
        return ranges;
	}

	private ArrayList<ByteRange> modifyFile(Object fileToModify, int fileSize, int ... bytesStart) throws IOException {
		
		ArrayList<ByteRange> ranges = new ArrayList<ByteRange>();

		for (int byteStart : bytesStart) {
			int byteEnd = byteStart+this.modificationSize; // Default 40 KB
			
			if (byteEnd > fileSize) {
				// TODO change this to modify always the same data size
				byteEnd = fileSize;
			}
			ranges.add(new ByteRange(byteStart, byteEnd));
		}
		
		return ranges;
	}
	
	private int getRealisticByteModification(int fileSize, ModificationPart part) {
		
		int filePart = fileSize/3;
		int multi;
		
		switch(part) {
		case B:
			multi = 0;
			break;
		case E:
			multi = 2;
			break;
		case M:
			multi = 1;
			break;
		default:
			multi = 2;
			break;
		}
		
		int byteStart = random.nextInt(filePart);
		byteStart = multi*filePart + byteStart;
		
		return byteStart;
	}
	
	private ModificationPart getModificationPart() {
		ModificationPart part;
		int randomPart = this.random.nextInt(100);
		
		if (randomPart < 38) {
			//Beginning
			part = ModificationPart.B;
		} else if (randomPart >= 38 && randomPart < 41) {
			//End
			part = ModificationPart.E;
		} else if (randomPart >= 41 && randomPart < 49){
			//Middle
			part = ModificationPart.M;
		} else if (randomPart >= 49 && randomPart < 59){
			//Beginning - End
			part = ModificationPart.BE;
		} else if (randomPart >= 59 && randomPart < 70){
			//Beginning - Middle
			part = ModificationPart.BM;
		} else if (randomPart >= 70 && randomPart < 71){
			//Middle - End
			part = ModificationPart.ME;
		} else {
			// BEM
			part = ModificationPart.BEM;
		}
		return part;
	}
}