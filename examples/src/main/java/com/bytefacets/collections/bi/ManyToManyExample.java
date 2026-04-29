// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.bi;

import com.bytefacets.collections.hash.StringIndexedSet;
import com.bytefacets.collections.hash.StringStringIndexedMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This is a small example using the CompactManyToMany to record class-to-student relationships.
 * Classes have many students, and students have many classes, and we can navigate either side of
 * the relationship.
 *
 * <p>Example output:
 *
 * <p>
 *
 * <pre>
 * English taught by Mr. Fletcher
 * Science taught by Mrs. White
 * Latin taught by Mr. Wilson
 * Math taught by Mr. Cooper
 * History taught by Mrs. Barber
 * Civics taught by Mrs. Carlson
 * Enrolling Sujith Kalra in Science
 * Enrolling Sujith Kalra in History
 * Enrolling Jose Garcia in English
 * Enrolling Jose Garcia in Math
 * Enrolling Suneeta Kumar in Science
 * Enrolling Suneeta Kumar in History
 * Enrolling Igor Chekov in English
 * Enrolling Igor Chekov in Science
 * Enrolling Sasha Petrovich in Science
 * Enrolling Sasha Petrovich in Math
 * Enrolling Alex Pedersen in English
 * Enrolling Alex Pedersen in Civics
 * Enrolling Rachel Schwartz in Latin
 * Enrolling Rachel Schwartz in History
 * Enrolling Luke Jorgens in History
 * Enrolling Luke Jorgens in Civics
 * Enrolling Jennifer O'Brian in Science
 * Enrolling Jennifer O'Brian in Latin
 * Enrolling Mary Johnson in Math
 * Enrolling Mary Johnson in Civics
 * Civics (3) taught by Mrs. Carlson:
 *    Mary Johnson
 *    Luke Jorgens
 *    Alex Pedersen
 *
 * History (4) taught by Mrs. Barber:
 *    Luke Jorgens
 *    Rachel Schwartz
 *    Suneeta Kumar
 *    Sujith Kalra
 *
 * Changing teachers.... History taught by Mr. Parker
 * History (4) taught by Mr. Parker:
 *    Luke Jorgens
 *    Rachel Schwartz
 *    Suneeta Kumar
 *    Sujith Kalra
 *
 * Suneeta Kumar (2):
 *    History taught by Mr. Parker
 *    Science taught by Mrs. White
 *
 * Luke Jorgens (2):
 *    Civics taught by Mrs. Carlson
 *    History taught by Mr. Parker
 * </pre>
 */
final class ManyToManyExample {
    private static final String[] CLASSES =
            new String[] {"English", "Science", "Latin", "Math", "History", "Civics"};
    private static final Random r = new Random(12345);

    private ManyToManyExample() {}

    public static void main(final String[] args) {
        final School school = new School();
        addClasses(school);
        Stream.of(
                        "Sujith Kalra",
                        "Jose Garcia",
                        "Suneeta Kumar",
                        "Igor Chekov",
                        "Sasha Petrovich",
                        "Alex Pedersen",
                        "Rachel Schwartz",
                        "Luke Jorgens",
                        "Jennifer O'Brian",
                        "Mary Johnson")
                .forEach(student -> enrollInSomeClasses(school, student));
        // navigate one side of the many-to-many
        school.printStudentsInClass("Civics");
        school.printStudentsInClass("History");
        // change the teacher - the entry for "History" is stable and needs no update registrations
        System.out.print("Changing teachers.... ");
        school.updateClass("History", "Mr. Parker");
        school.printStudentsInClass("History");

        // navigate the other side of the many-to-many
        school.printClassesForStudent("Suneeta Kumar");
        school.printClassesForStudent("Luke Jorgens");
    }

    // enrolls the student in 2 random classes
    private static void enrollInSomeClasses(final School school, final String student) {
        pickRandom(2, CLASSES.length).forEach(classIx -> school.enroll(CLASSES[classIx], student));
    }

    // registers classes and teachers
    private static void addClasses(final School school) {
        school.updateClass("English", "Mr. Fletcher");
        school.updateClass("Science", "Mrs. White");
        school.updateClass("Latin", "Mr. Wilson");
        school.updateClass("Math", "Mr. Cooper");
        school.updateClass("History", "Mrs. Barber");
        school.updateClass("Civics", "Mrs. Carlson");
    }

    private static final class School {
        private final StringIndexedSet students = new StringIndexedSet(16);
        private final StringStringIndexedMap classToTeacher = new StringStringIndexedMap(16);
        private final CompactManyToMany registrations = new CompactManyToMany();

        private void updateClass(final String className, final String teacherName) {
            System.out.printf("%s taught by %s%n", className, teacherName);
            classToTeacher.put(className, teacherName);
        }

        private void enroll(final String className, final String studentName) {
            System.out.printf("Enrolling %s in %s%n", studentName, className);
            final int classEntry = classToTeacher.lookupEntry(className);
            if (classEntry != -1) {
                final int studentEntry = students.add(studentName);
                // the mappings can be iterated from either side
                registrations.put(classEntry, studentEntry);
            } else {
                System.out.printf("Class not found: %s%n", className);
            }
        }

        private void printStudentsInClass(final String className) {
            // find the entry for the class name in the map
            final int classEntry = classToTeacher.lookupEntry(className);
            if (classEntry == -1) {
                System.out.printf("%s (0): NOT FOUND", className);
            } else {
                final Mapping classEnrollment = registrations.withLeft(classEntry);
                // use the entry to directly access the teacher mapped to this class
                final String teacher = classToTeacher.getValueAt(classEntry);
                System.out.printf(
                        "%s (%d) taught by %s:%n", className, classEnrollment.count(), teacher);
                // iterate all the studentEntries associated with the classEntry
                classEnrollment.forEachValue(
                        studentEntry -> {
                            System.out.printf("   %s%n", students.getKeyAt(studentEntry));
                        });
            }
            System.out.println();
        }

        private void printClassesForStudent(final String student) {
            // find the entry for the student name in the map
            final int studentEntry = students.lookupEntry(student);
            if (studentEntry == -1) {
                System.out.printf("%s (0): NOT FOUND", studentEntry);
            } else {
                // contextualize the mappings for the given studentEntry
                final Mapping studentClasses = registrations.withRight(studentEntry);
                System.out.printf("%s (%d):%n", student, studentClasses.count());
                studentClasses.forEachValue(
                        classEntry -> {
                            // use the entry to directly access the class name
                            final String className = classToTeacher.getKeyAt(classEntry);
                            // use the entry to directly access the teacher mapped to this class
                            final String teacher = classToTeacher.getValueAt(classEntry);
                            System.out.printf("   %s taught by %s%n", className, teacher);
                        });
            }
            System.out.println();
        }
    }

    private static Set<Integer> pickRandom(final int count, final int max) {
        final Set<Integer> result = new HashSet<>();
        while (result.size() < count) {
            result.add(r.nextInt(max));
        }
        return result;
    }
}
