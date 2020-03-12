package cmd.csp.utils;

import java.awt.Rectangle;

import net.sourceforge.tess4j.Word;

public class TCDWord extends Word {
	private Integer intPage=0;

	private String fontName = "";
	private Boolean bold = false;
	private Boolean italic = false;
	private Boolean underlined = false;
	private Boolean monospace = false;
	private Boolean serif = false;
	private Boolean smallcaps = false;
	private Integer pointSize = 0;
	private Integer fontId = 0;
	private Integer blocktype = 0;
	private Integer numeric = 0;
	
	
	public TCDWord(Integer iPage, String text, float confidence, Rectangle boundingBox) {
		super(text, confidence, boundingBox);
		intPage=iPage;
		// TODO Auto-generated constructor stub
	}
    public Integer getPage() {
        return intPage;
    }
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public Boolean getBold() {
		return bold;
	}
	public void setBold(Boolean bold) {
		this.bold = bold;
	}
	public Boolean getItalic() {
		return italic;
	}
	public void setItalic(Boolean italic) {
		this.italic = italic;
	}
	public Boolean getUnderlined() {
		return underlined;
	}
	public void setUnderlined(Boolean underlined) {
		this.underlined = underlined;
	}
	public Boolean getMonospace() {
		return monospace;
	}
	public void setMonospace(Boolean monospace) {
		this.monospace = monospace;
	}
	public Boolean getSerif() {
		return serif;
	}
	public void setSerif(Boolean serif) {
		this.serif = serif;
	}
	public Boolean getSmallcaps() {
		return smallcaps;
	}
	public void setSmallcaps(Boolean smallcaps) {
		this.smallcaps = smallcaps;
	}
	public Integer getPointSize() {
		return pointSize;
	}
	public void setPointSize(Integer pointSize) {
		this.pointSize = pointSize;
	}
	public Integer getFontId() {
		return fontId;
	}
	public void setFontId(Integer fontId) {
		this.fontId = fontId;
	}
	public Integer getBlocktype() {
		return blocktype;
	}
	public void setBlocktype(Integer blocktype) {
		this.blocktype = blocktype;
	}
	public Integer getNumeric() {
		return numeric;
	}
	public void setNumeric(Integer numeric) {
		this.numeric = numeric;
	}
}
