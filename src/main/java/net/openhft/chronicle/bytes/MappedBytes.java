/*
 * Copyright 2015 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.openhft.chronicle.bytes;

import net.openhft.chronicle.core.OS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by peter.lawrey on 24/02/15.
 */
public class MappedBytes extends AbstractBytes<Void> {
    private final MappedFile mappedFile;

    // assume the mapped file is reserved already.
    MappedBytes(MappedFile mappedFile) {
        super(NoBytesStore.noBytesStore());
        this.mappedFile = mappedFile;
        clear();
    }

    public static MappedBytes mappedBytes(String filename, long chunkSize) throws FileNotFoundException {
        return mappedBytes(new File(filename), chunkSize);
    }

    public static MappedBytes mappedBytes(File file, long chunkSize) throws FileNotFoundException {
        MappedFile rw = new MappedFile(file, chunkSize, OS.pageSize());
        MappedBytes bytes = new MappedBytes(rw);
        return bytes;
    }

    @Override
    public BytesStore<Bytes<Void>, Void> copy() {
        return NativeBytes.copyOf(this);
    }

    @Override
    public long capacity() {
        return mappedFile == null ? 0L : mappedFile.capacity();
    }

    @Override
    public void reserve() throws IllegalStateException {
        super.reserve();
    }

    @Override
    public void release() throws IllegalStateException {
        super.release();
    }

    @Override
    public long refCount() {
        return Math.max(super.refCount(), mappedFile.refCount());
    }

    @Override
    protected void readCheckOffset(long offset, long adding) {
        checkOffset(offset);
    }

    @Override
    protected void writeCheckOffset(long offset, long adding) {
        checkOffset(offset);
    }

    private void checkOffset(long offset) {
        if (!bytesStore.inStore(offset)) {
            BytesStore oldBS = bytesStore;
            try {
                bytesStore = mappedFile.acquireByteStore(offset);
                oldBS.release();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    @Override
    public long start() {
        return 0L;
    }

    @Override
    public Bytes writeLong(long offset, long i) {
        return super.writeLong(offset, i);
    }

    @Override
    protected void performRelease() {
        super.performRelease();
        mappedFile.close();
    }

    @Override
    public boolean isElastic() {
        return true;
    }
}
