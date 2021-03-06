package com.grack.hidecpu.assembler;

public class Line {
	private String label;
	private Opcode opcode;
	private Value value;
	private OpSource source;
	private OpTarget target;
	private BranchType branchType;

	public Line(String label) {
		this.label = label;
	}

	public Line(String label, Opcode opcode, OpSource source, OpTarget target, Value value) {
		this.label = label;
		this.opcode = opcode;
		this.source = source;
		this.target = target;
		this.value = value;
	}

	public Line(Opcode opcode, OpSource source, OpTarget target, Value value) {
		this.opcode = opcode;
		this.source = source;
		this.target = target;
		this.value = value;
	}

	public Line(String label, Opcode opcode, BranchType branchType, Value value) {
		this.label = label;
		this.opcode = opcode;
		this.branchType = branchType;
		this.value = value;
	}

	public Line(Opcode opcode, BranchType branchType, Value value) {
		this.opcode = opcode;
		this.branchType = branchType;
		this.value = value;
	}

	public Line(Value value) {
		this.opcode = Opcode.DATA;
		this.source = OpSource.CONSTANT;
		this.target = OpTarget.R0;
		this.value = value;
	}

	public Line(String label, Value value) {
		this.label = label;
		this.opcode = Opcode.DATA;
		this.source = OpSource.CONSTANT;
		this.target = OpTarget.R0;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Opcode getOpcode() {
		return opcode;
	}

	public void setOpcode(Opcode opcode) {
		this.opcode = opcode;
	}

	public OpSource getSource() {
		return source;
	}

	public OpTarget getTarget() {
		return target;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public boolean isEmpty() {
		return label == null && opcode == null;
	}

	public int[] assemble() {
		if (opcode == Opcode.DATA)
			return new int[] { (Integer)value.getValue() };
		
		int op = 0;
		op |= opcode.ordinal() << 4;
		op |= (branchType == null) ? 0 : (branchType.ordinal() << 0);
		op |= (target == null) ? 0 : (target.ordinal() << 2);
		op |= (source == null) ? 0 : (source.ordinal() << 0);
		
		int val = 0;
		val |= (int) (value == null ? 0 : value.getValue());

		return new int[] { val, op };
	}

	@Override
	public String toString() {
		return toString(-1);
	}
	
	public String toString(int line) {
		// Empty line
		if (isEmpty())
			return "";

		String s = label == null ? "" : label + ":";
		if (opcode != null) {
			if (label != null)
				s += '\n';
			if (line != -1)
				s += line;
			s += '\t';

			String src = "<null>";
			if (source != null) {
				switch (source) {
				case CONSTANT:
					src = value.toString();
					break;
				case CONSTANT_LOAD:
					src = '[' + value.toString() + ']';
					break;
				case R0_RELATIVE_LOAD:
					src = "[r0 + " + value.toString() + ']';
					break;
				case R1_RELATIVE_LOAD:
					src = "[r1 + " + value.toString() + ']';
					break;
				}
			}

			switch (opcode) {
			case LOAD:
				s += "mov\t" + target.name().toLowerCase() + ", " + src;
				break;
			case STORE:
				s += "mov\t" + src + ", " + target.name().toLowerCase();
				break;
			case ROL:
			case ROR:
			case NOT:
				s += "mov\t" + target.name().toLowerCase() + ", "
						+ opcode.name().toLowerCase() + " " + src;
				break;
			case BRA:
				s += "b" + branchType.name().toLowerCase() + "\t"
						+ value.toString();
				break;
			case JUMP:
			case DATA:
				s += opcode.name().toLowerCase() + "\t" + src;
				break;
			case CARRY:
				if ((int)value.getValue() == 0)
					s += "clc";
				else
					s += "sec";
				break;
			default:
				s += opcode.name().toLowerCase() + "\t"
						+ target.name().toLowerCase() + ", " + src;
			}
		}

		return s;
	}

	public int size() {
		if (opcode == null)
			return 0;
		return opcode == Opcode.DATA ? 1 : 2;
	}
}
