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
	
	//private Logger logger = Logger.getLogger( DatasetGenerator.class.getName() );
	public enum ModificationPart {B, E, M, BE, BM, ME, BEM};
	public enum Operation {ADD, UPDATE, REMOVE};
	
	private Random random;
	private int maxModificationSize;
	
	public DatasetGenerator() {
		this.random = new Random();
		this.maxModificationSize = Config.getMaxModificationSize();
	}
	
	public abstract void generateDataset(String datasetFile, List<SimpleFile> files);

	protected ArrayList<ByteRange> generateUpdate(int fileSize) throws IOException {
        
        ModificationPart modificationPart = this.getModificationPart();
        
        int bs1, bs2, bs3 = 0;
        
        ArrayList<ByteRange> ranges = null;
        
        switch (modificationPart){
        case B:
        case E:
        case M:
            bs1 = this.getRealisticByteModification(fileSize, modificationPart);
            ranges = this.modifyFile(fileSize, bs1);
            break;
        case BE:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
            ranges = this.modifyFile(fileSize, bs1, bs2);
            break;
        case BM:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.M);
            ranges = this.modifyFile(fileSize, bs1, bs2);
            break;
        case ME:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.M);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
            ranges = this.modifyFile(fileSize, bs1, bs2);
            break;
        case BEM:
            bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
            bs2 = this.getRealisticByteModification(fileSize, ModificationPart.M);
            bs3 = this.getRealisticByteModification(fileSize, ModificationPart.E);
            ranges = this.modifyFile(fileSize, bs1, bs2, bs3);
            break;
        }
        
        return ranges;
	}

	private ArrayList<ByteRange> modifyFile(int fileSize, int ... bytesStart) throws IOException {
		
		ArrayList<ByteRange> ranges = new ArrayList<ByteRange>();

		for (int byteStart : bytesStart) {
			int byteEnd;
			if (byteStart == -1 || byteStart == 0) {
				byteEnd = getModificationSize();
			} else {
				byteEnd = byteStart+getModificationSize(); // Default 40 KB
			}
			
			ranges.add(new ByteRange(byteStart, byteEnd));
		}
		
		return ranges;
	}
	
	private int getRealisticByteModification(int fileSize, ModificationPart part) {
		
		int byteStart;
		
		switch(part) {
		case B:
			byteStart = 0;
			break;
		case E:
			byteStart = -1;
			break;
		case M:
		default:
			int offset = (int)(fileSize*0.1F);
			int start = offset;
			int end = fileSize-offset;
			byteStart = start + random.nextInt(end - start + 1);
			break;
		}
		
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
	
	private int getModificationSize() {
		return this.random.nextInt(maxModificationSize);
	}
	
	public int getBytesAdded(ArrayList<ByteRange> ranges) {
		
		int bytesAdded = 0;
		
		for (ByteRange range : ranges) {
			if (range.getByteStart() == 0 || range.getByteStart() == -1) {
				bytesAdded += range.getByteEnd();
			} /*else {
				// TODO IMPORTANT: In modification bytes are not added, are modified!!
				bytesAdded += (range.getByteEnd() - range.getByteStart());
			}*/
		}
		
		return bytesAdded;
	}
}