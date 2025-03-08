package com.galacticware.griddle.domain.model.collection

import java.util.concurrent.atomic.AtomicReference

class ConcurrentStack<T> {

    private val stack = AtomicReference<Node<T>?>()

    data class Node<T>(val value: T, val next: Node<T>?)

    fun push(value: T) {
        val newNode = Node(value, stack.get())
        while (!stack.compareAndSet(stack.get(), newNode)) {
            // Retry if another thread has modified the stack
        }
    }

    fun pop(): T? {
        while (true) {
            val head = stack.get() ?: return null
            val newHead = head.next
            if (stack.compareAndSet(head, newHead)) {
                return head.value
            }
        }
    }

    fun peek(): T? {
        return stack.get()?.value
    }

    fun isEmpty(): Boolean {
        return stack.get() == null
    }

    fun remove(t: T) {
        val tempStack = ConcurrentStack<T>()
        while (!isEmpty()) {
            val value = pop()
            if (value != t) {
                tempStack.push(value!!)
            }
        }
        while (!tempStack.isEmpty()) {
            push(tempStack.pop()!!)
        }
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    fun contains(t: T): Boolean {
        val tempStack = ConcurrentStack<T>()
        var found = false
        while (!isEmpty()) {
            val value = pop()
            if (value == t) {
                found = true
            }
            tempStack.push(value!!)
        }
        while (!tempStack.isEmpty()) {
            push(tempStack.pop()!!)
        }
        return found
    }
}