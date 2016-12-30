package reparator;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import spoon.Launcher;
import util.Constants;

public class TransformationsTest {
	private static String projectName = "demotransformations";
	private static String packageName = "com.demotransformations";
	
	private static File rootWithAllVersions = new File(
			new StringBuilder().append("resources").append(File.separator).append(projectName).toString());
	private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	@BeforeClass
	public static void init() {
		// Création de la liste comprenant les versions
		LinkedList<VersionSniper> snipers = new LinkedList<VersionSniper>();
		String projectPath = new StringBuilder().append(rootWithAllVersions).append(File.separator).append(projectName)
				.toString();
		for (int i = 0; i < 3; i++) {
			snipers.add(new VersionSniper(projectPath, Constants.srcMainJava, null, i));
		}
		// On lance la transformation avec Spoon, on compile et on met dans le
		// target
		Launcher spoon = new Launcher();
		spoon.addProcessor(new MethodVersioningProcessor(snipers));
		spoon.run(new String[] { "-i",
				new StringBuilder().append(projectPath).append(File.separator).append(Constants.srcMainJava).toString(),
				"-d", Constants.targetMainPath,
				"--output-type","nooutput",
				"--compile"
		});
		// Recherche du ou des "Class" du package com.demotransformations afin
		// de pouvoir faire les tests avec Reflections
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(
						false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))));

		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		// On récupère dans la Map toutes les classes du package
		// com.demotransformations
		for (Class<?> c : allClasses) {
			if (c.getPackage().getName().startsWith(packageName)) {
				classes.put(c.getName(), c);
			}
		}
	}
	
	@Test
	public void testAttributeNames() throws Exception {
		Class<?> classTransformation1 = classes.get(packageName + ".Transformation1");
		// Exception if field not found
		classTransformation1.getDeclaredField("method1_version");
		classTransformation1.getDeclaredField("method1_version_max");
		classTransformation1.getDeclaredField("method1_int_version");
		classTransformation1.getDeclaredField("method1_int_version_max");
		classTransformation1.getDeclaredField("method2_version");
		classTransformation1.getDeclaredField("method2_version_max");
		classTransformation1.getDeclaredField("method3_int_version");
		classTransformation1.getDeclaredField("method3_int_version_max");
		classTransformation1.getDeclaredField("method5_java0util0List0java0lang0String0_version");
		classTransformation1.getDeclaredField("method5_java0util0List0java0lang0String0_version_max");
	}
	
	@Test
	public void testVersionMax() throws Exception {
		Class<?> classTransformation1 = classes.get(packageName + ".Transformation1");
		Field method1_version_max = classTransformation1.getDeclaredField("method1_version_max");
		assertEquals(1,method1_version_max.get(Integer.class));
		
		Field method2_version_max = classTransformation1.getDeclaredField("method2_version_max");
		assertEquals(2,method2_version_max.get(Integer.class));
	}
	
	@Test
	public void testMethodSignatures() throws Exception {
		Class<?> classTransformation1 = classes.get(packageName + ".Transformation1");
		// Exception if method not found
		Method method = null;
		// METHOD 1
		// Public
		method = classTransformation1.getDeclaredMethod("method1",int.class);
		assertTrue(Modifier.isPublic(method.getModifiers()));
		// Private
		method = classTransformation1.getDeclaredMethod("method1_0");
		assertTrue(Modifier.isPrivate(method.getModifiers()));
		method = classTransformation1.getDeclaredMethod("method1_1");
		assertTrue(Modifier.isPrivate(method.getModifiers()));
		try {
			classTransformation1.getDeclaredMethod("method1_2");
			// Fail because this method doesn't exist
			fail("La méthode method1_2 ne devrait pas exister !");
		} catch (NoSuchMethodException nsme) {
			// Ne rien faire
		}
		// Exception if not found
		classTransformation1.getDeclaredMethod("method2_0");
		classTransformation1.getDeclaredMethod("method2_1");
		classTransformation1.getDeclaredMethod("method2_2");
		classTransformation1.getDeclaredMethod("method3_0",int.class);
		classTransformation1.getDeclaredMethod("method3_1",int.class);
		classTransformation1.getDeclaredMethod("method3_2",int.class);
		classTransformation1.getDeclaredMethod("method5_0",List.class);
		classTransformation1.getDeclaredMethod("method5_1",List.class);
		classTransformation1.getDeclaredMethod("method5_2",List.class);
	}
	
	@Test
	public void testMethodBody() throws Exception {
		Class<?> classTransformation1 = classes.get(packageName + ".Transformation1");
		
		Field method2_version = classTransformation1.getDeclaredField("method2_version");
		method2_version.setAccessible(true);
		
		Method method2 = classTransformation1.getMethod("method2");
		Object o = classTransformation1.newInstance();
		
		// The version is 0 first
		assertEquals(0,method2.invoke(o));
		method2_version.set(method2_version,new Integer(1));
		assertEquals(1,method2.invoke(o));
		method2_version.set(method2_version,new Integer(2));
		assertEquals(2,method2.invoke(o));
	}
}
