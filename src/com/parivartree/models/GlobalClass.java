package com.parivartree.models;

import java.util.Stack;

import android.app.Application;

public class GlobalClass extends Application {
Stack<String> nodeIdStack = new Stack<String>();

public Stack<String> getNodeIdStack() {
	return nodeIdStack;
}

public void setNodeIdStack(Stack<String> nodeIdStack) {
	this.nodeIdStack = nodeIdStack;
}
}
