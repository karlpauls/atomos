/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.atomos.framework.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.connect.ConnectContent;

public class JarConnectContent implements ConnectContent
{
    final ZipFile zipFile;

    public JarConnectContent(ZipFile zipFile)
    {
        this.zipFile = zipFile;
    }

    @Override
    public void open() throws IOException
    {
        // do nothing
    }

    @Override
    public void close() throws IOException
    {
        // do nothing
    }

    @Override
    public Optional<ClassLoader> getClassLoader()
    {
        return Optional.of(getClass().getClassLoader());
    }

    @Override
    public Iterable<String> getEntries() throws IOException
    {
        return () -> new Iterator<String>()
        {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            @Override
            public boolean hasNext()
            {
                return entries.hasMoreElements();
            }

            @Override
            public String next()
            {
                return entries.nextElement().getName();
            }
        };
    }

    @Override
    public Optional<ConnectEntry> getEntry(String name)
    {
        ZipEntry entry = zipFile.getEntry(name);
        if (entry != null)
        {
            return Optional.of(new JarConnectEntry(entry));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Map<String, String>> getHeaders()
    {
        return Optional.empty();
    }

    class JarConnectEntry implements ConnectEntry
    {
        final ZipEntry entry;

        public JarConnectEntry(ZipEntry entry)
        {
            this.entry = entry;
        }

        @Override
        public long getContentLength()
        {
            return entry.getSize();
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            return zipFile.getInputStream(entry);
        }

        @Override
        public long getLastModified()
        {
            return entry.getTime();
        }

        @Override
        public String getName()
        {
            return entry.getName();
        }

    }
}
