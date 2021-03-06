package org.lov.rdf2mongo.iterators;

import java.util.Collection;
import java.util.Iterator;

import org.lov.objects.Vocabulary;
import org.lov.rdf2mongo.describers.VocabularyDescriber;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Utility class that implements an iterator over indexable
 * {@link Vocabulary}s by calling a {@link VocabularyDescriber}
 * on every {@link Resource} in a collection.
 * 
 * @author Pierre-Yves Vandenbussche
 */
public class VocabulariesDescriberIterator implements Iterator<Vocabulary> {
	private final Iterator<Resource> it;
	private final VocabularyDescriber describer;
	
	public VocabulariesDescriberIterator(Collection<Resource> resources, VocabularyDescriber describer) {
		this.it = resources.iterator();
		this.describer = describer;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Vocabulary next() {
		Resource resource = it.next();
		return  describer.describe(resource);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
